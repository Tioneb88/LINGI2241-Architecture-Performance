/*
 * LINGI2241 - Architecture Performance and Computer Systems
 * Project : Measurement & Modeling 
 * Author : Benoît Michel - 23971600
 * Date : december 2020
 * Based on https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
 */


import java.io.*;
import java.net.*;
// optimized ...
import java.util.List;
import java.util.Date;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
// ... optimized

import utils.Buffer;
import utils.Request;
import utils.Cache;

/*
 * Optimized server able to receive client requests and respond to them adequately (with cache).
 */
public class OptimizedServer {

    /*
     * Main method to launch the server.
     * @param dbFilename : the path to the database text file
     * @param portNumber : the port used to establish the connection with the client
     * @param nbThreads : the number of threads wanted for the server
     * @param resultFilename : the name for the file with all the results
     * @return None
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        // Checking of the usage
        if (args.length != 4) {
            System.err.println("Usage: java OptimizedServer <database text file> <port number> <number of threads> <result filename>");
            System.exit(1);
        }

        // Arguments recovery and server creation
        final int N_THREADS = Integer.parseInt(args[2]);
        String resultFilename = args[3];
        OptimizedProtocol protocol = new OptimizedProtocol(fileToArray(args[0]), 30, 10); // optimized protocol
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[1]));
        Buffer<Request> buf = new Buffer<>(20000); // Arbitrary buffer capacity of 20000

        // Lists to store the time for the queue and for the service
        List<Long> qTime = new ArrayList<>();
        List<Long> sTime = new ArrayList<>();

        // client read and write for the requests
        Socket clientSocket = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

        System.out.println("Optimized server started at " + InetAddress.getLocalHost());

        // Definition of the threads
        Thread[] threads = new Thread[N_THREADS];
        for (int i=0; i < N_THREADS; i++) {
            threads[i] = new Thread(() -> {
                try {
                    Request request = buf.take();
                    while (request != null) {
                        String value = request.getValue();

                        // Signal to stop the thread
                        if (value.equals("Stop")) break;

                        request.endWait(new Date());
                        request.startTreat(new Date());
                        String output = request.getSentByClient().getTime() + ";" + protocol.processValue(value);
                        request.endTreat(new Date());

                        qTime.add(request.waitTime());
                        sTime.add(request.treatTime());

                        synchronized (writer) {
                            writer.println(output);
                            writer.flush();
                        }

                        request = buf.take();
                    }
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
            });
            threads[i].start();
            System.out.println("Thread " + i + " started !");
        }

        // Read lines and attribute requests to threads
        try {
            String line = reader.readLine();
            while (line != null) {
                String[] splitLine = line.split(";", 2);
                Request request = new Request(splitLine[1]);
                request.setSentByClient(new Date(Long.parseLong(splitLine[0])));
                request.startWait(new Date());

                if (!buf.add(request)) System.err.println("The buffer is full, a request has been dropped !");

                line = reader.readLine();
            }

            // Adds final stop messages to all the threads
            for (int i = 0; i < N_THREADS; i++) {
                if (!buf.add(new Request("Stop"))) System.err.println("A thread is unstoppable !");
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        // Waits all the threads to finish
        for (int i = 0; i < N_THREADS; i++) {
            threads[i].join();
        }

        // Close everything
        writer.close();
        reader.close();
        serverSocket.close();
        clientSocket.close();

        // Writes the results to output files
        saveResults(qTime, resultFilename + "_queue.txt");
        saveResults(sTime, resultFilename + "_service.txt");

        System.out.println("Optimized server finished !");
    }


    /*
     * Reads the file and adds all the lines to a hashmap with category as key and then returns as array of strings
     * @param filename : the file from which to read the lines
     * @return array : an array of the strings (lines) in the file, indexed according to a hashmap
     */
    public static String[][] fileToArray(String filename) {
        try {
            String[] line;
            Map<Integer, Set<String>> hashmap = new HashMap<>(); // optimized
            Scanner cursor = new Scanner(new File(filename));

            while (cursor.hasNextLine()) {
                line = cursor.nextLine().split("@@@");
                int key = Integer.parseInt(line[0]);
                if (!hashmap.containsKey(key)) {
                    hashmap.put(key, new HashSet<>());
                }
                hashmap.get(key).add(line[1]);
            }
            cursor.close();

            String[][] array = new String[hashmap.size()][];
            for (Map.Entry<Integer, Set<String>> elem : hashmap.entrySet()) {
                array[elem.getKey()] = elem.getValue().toArray(new String[0]);
            }

            System.out.println("Lines read from " + filename);
            return array;

        } catch (IOException e) {
            System.err.println("Error with the file to read !");
            return null;
        }
    }

    /*
     * Saves the results of the list in the file.
     * @param list : list of results
     * @param filename : the file where to write the results
     * @return None
     */
    public static void saveResults(List<Long> list, String filename) {
        try {
            FileWriter writer = new FileWriter(filename);

            for (long line : list) {
                writer.write(line + "\n");
            }

            writer.close();
            System.out.println("Results written in " + filename);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }


    /*
     * Internal class to process the requests of the server.
     */
    public static class OptimizedProtocol {
        private final Cache cache;
        private final String[][] dbMap;

        /*
         * Constructs an object OptimizedProtocol with the lines of the database given.
         * @param dbMap : the map of the lines of the database
         * @param size : the maximal size of the cache
         * @param threshold : the maximal threshold of frequency for the cache
         * @return None
         */
        public OptimizedProtocol(String[][] dbMap, int size, float threshold) {
            this.cache = new Cache(size, threshold);
            this.dbMap = dbMap;
        }

        /*
         * Process the request with the database and the cache and returns the adequate response.
         * @param request : the request to process
         * @return response : the response to the request
         */
        public String processValue(String request) throws InterruptedException {
            if (request == null) return null;

            String response;
            String[] splitRequest = request.split(";", 2);
            if (splitRequest.length != 2) {
                System.err.println("The request format is incorrect ! Process : impossible !");
                return null;
            }

            // Preparation and extraction
            String[] types = splitRequest[0].split(",");
            String regex = splitRequest[1];
            Pattern pattern = Pattern.compile(regex);

            // Checking if the request is already in the cache // optimized
            response = this.cache.get(request);
            if (response != null) return response;

            // Search in the hashmap
            int[] intTypes;
            if (types[0].equals("") && types.length == 1) {
                // If no types we search them all
                intTypes = new int[this.dbMap.length];
                for (int i = 0; i < intTypes.length; i++) {
                    intTypes[i] = i;
                }
            } else {
                intTypes = new int[types.length];
                for (int i = 0; i < types.length; i++) {
                    intTypes[i] = Integer.parseInt(types[i]);
                }
            }

            // Concurrent search for each independent type
            StringBuilder builder = new StringBuilder();
            for (int key : intTypes) {
                for (String value : OptimizedProtocol.this.dbMap[key]) {
                    Matcher matcher = pattern.matcher(value);
                    if (matcher.find()) {
                        builder.append(key).append("@@@").append(value).append("\n");
                    }
                }
            }
            response = "";
            if (builder.length() > 0)
                response = builder.toString();

            this.cache.add(request, response); // optimized
            return response;
        }
    }


}