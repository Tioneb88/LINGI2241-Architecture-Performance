/*
 * LINGI2241 - Architecture Performance and Computer Systems
 * Project : Measurement & Modeling 
 * Author : Benoît Michel - 23971600
 * Date : december 2020
 * Based on https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import utils.Buffer;
import utils.Request;

/*
 * Basic server able to receive client requests and respond to them adequately.
 */
public class BasicServer {

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
            System.err.println("Usage: java BasicServer <database text file> <port number> <number of threads> <result filename>");
            System.exit(1);
        }

        // Arguments recovery and server creation
        final int N_THREADS = Integer.parseInt(args[2]);
        String resultFilename = args[3];
        BasicServer.BasicProtocol protocol = new BasicServer.BasicProtocol(fileToArray(args[0]));
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[1]));
        Buffer<Request> buf = new Buffer<>(20000); // Arbitrary buffer capacity of 20000

        // Lists to store the time for the queue and for the service
        List<Long> qTime = new ArrayList<>();
        List<Long> sTime = new ArrayList<>();

        // client read and write for the requests
        Socket clientSocket = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

        System.out.println("Basic server started at " + InetAddress.getLocalHost());

        // Definition of the threads
        Thread[] threads = new Thread[N_THREADS];
        for (int i=0; i<N_THREADS; i++) {
            threads[i] = new Thread(() -> {
                try {
                    Request request = buf.take();
                    while (request != null) {
                        String value = request.getValue();

                        // Signal to stop the thread
                        if (value.equals("Stop")) break;

                        request.endWait(new Date());
                        request.startTreat(new Date());
                        String output = request.getSentByClient().getTime() + ";" + protocol.process(value);
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

        System.out.println("Basic server finished !");
    }

    /*
     * Reads the file and adds all the lines to a list of strings.
     * @param filename : the file from which to read the lines
     * @return array : an array of the strings (lines) in the file
     */
    public static String[][] fileToArray(String filename) {
        try {
            String[] line;
            ArrayList<String[]> list = new ArrayList<>();
            Scanner cursor = new Scanner(new File(filename));

            while (cursor.hasNextLine()) {
                line = cursor.nextLine().split("@@@");
                list.add(line);
            }

            cursor.close();
            System.out.println("Lines read from " + filename);
            return list.toArray(new String[list.size()][list.get(0).length]);

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
    public static class BasicProtocol {
        private final String[][] DBLines;

        /*
         * Constructs an object BasicProtocol with the lines of the database given.
         * @param DBlines : the lines of the database
         * @return None
         */
        public BasicProtocol(String[][] lines) {
            this.DBLines = lines;
        }

        /*
         * Process the request with the database and returns the adequate response.
         * @param request : the request to process
         * @return response : the response to the request
         */
        public String process(String request) {
            if (request == null) return null;

            String[] splitRequest = request.split(";", 2);
            if (splitRequest.length != 2) {
                System.err.println("The request format is incorrect ! Process : impossible !");
                return null;
            }

            // Preparation and extraction
            String[] types = splitRequest[0].split(",");
            String regex = splitRequest[1];
            Pattern pattern = Pattern.compile(regex);

            // Linear search
            StringBuilder response = new StringBuilder();
            for (int i = 0; i < this.DBLines.length; i++) {
                if (types.length == 0) {
                    Matcher matcher = pattern.matcher(this.DBLines[i][1]);
                    if (matcher.find()) {
                        response.append(this.DBLines[i][0]).append("@@@").append(this.DBLines[i][1]).append("\n");
                    }
                } else {
                    for (String type : types) {
                        if (this.DBLines[i][0].equals(type)) {
                            Matcher matcher = pattern.matcher(this.DBLines[i][1]);
                            if (matcher.find()) {
                                response.append(this.DBLines[i][0]).append("@@@").append(this.DBLines[i][1]).append("\n");
                                break;
                            }
                        }
                    }
                }
            }
            return response.toString();
        }
    }
}