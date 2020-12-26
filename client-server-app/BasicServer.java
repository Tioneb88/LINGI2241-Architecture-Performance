/*
 * LINGI2241 - Architecture Performance and Computer Systems
 * Project : Measurement & Modeling 
 * Author : Benoît Michel - 23971600
 * Date : december 2020
 * Based on https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
 */

import utils.Buffer;
import utils.Request;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 4 && args.length != 5) {
            System.err.println("Usage: java SimpleServer <port number> <database text file> <number of threads> <verbose> [result text file]");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        String dbfile = args[1];
        final int N_THREADS = Integer.parseInt(args[2]);
        final boolean verbose = Boolean.parseBoolean(args[3]);
        String resultsFile = (args.length == 5) ? args[4] : null;

        ServerSocket serverSocket = new ServerSocket(portNumber);
        Buffer<Request> buffer = new Buffer<>(10000);
        SimpleServer.SimpleServerProtocol ssp = new SimpleServer.SimpleServerProtocol(initArray(dbfile));

        if (verbose)
            System.out.println("Server is up at "+InetAddress.getLocalHost());

        Socket clientSocket = serverSocket.accept();
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        List<Long> queueTimes = new ArrayList<>();
        List<Long> serviceTimes = new ArrayList<>();


        // Initializing worker threads
        Thread[] threads = new Thread[N_THREADS];
        for (int i=0; i < N_THREADS; i++) {
            threads[i] = new Thread(() -> {
                try {
                    Request r;
                    while ((r = buffer.take()) != null) {
                        if (r.getRequestValue().equals("Done")) break;
                        r.setFinishedQueuingTime(new Date());
                        r.setStartingToTreatRequestTime(new Date());
                        String outputLine = ssp.processInput(r.getRequestValue());
                        r.setFinishedTreatingRequestTime(new Date());
                        if (r.getSentByClientTime() != null) {
                            outputLine = r.getSentByClientTime().getTime() + ";" + outputLine;
                        }
                        synchronized (out) {
                            out.println(outputLine);
                            out.flush();
                        }
                        if (resultsFile != null) {
                            logResponse(r.computeQueuingTime(), queueTimes);
                            logResponse(r.computeServiceTime(), serviceTimes);
                        }
                    }
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
            });
            // starting the threads right away
            threads[i].start();
        }

        // Loop to fill buffer
        try {
            String fromClient;
            while ((fromClient = in.readLine()) != null) {
                Request received;
                if (resultsFile != null) {
                    String[] splitRequest = fromClient.split(";", 2);
                    received = new Request(splitRequest[1]);
                    received.setSentByClientTime(new Date(Long.parseLong(splitRequest[0])));
                } else {
                    received = new Request(fromClient);
                }
                received.setStartingQueuingTime(new Date());
                if (!buffer.add(received)) System.err.println("Full buffer, had to drop a request");
            }
            for (int i = 0; i < N_THREADS; i++) {
                if (!buffer.add(new Request("Done"))) System.err.println("Could not stop a thread");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        // Waiting for the threads to finish to return
        for (int i = 0; i < N_THREADS; i++) {
            threads[i].join();
        }

        in.close();
        out.close();
        serverSocket.close();
        clientSocket.close();

        if (resultsFile != null) {
            writeResultsFile(queueTimes, resultsFile+"_queue.txt", verbose);
            writeResultsFile(serviceTimes, resultsFile+"_service.txt", verbose);
        }

    }

    public static synchronized void logResponse(long time, List<Long> times) {
        times.add(time);
    }

    public static void writeResultsFile(List<Long> resultsList, String outputFilename, boolean verbose) {
        try {
            FileWriter outputWriter = new FileWriter(outputFilename);
            for (long line : resultsList) {
                outputWriter.write(line+"\n");
            }
            outputWriter.close();
            if (verbose)
                System.out.println("Saved results to "+outputFilename);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static String[][] initArray(String filename) {
        try {
            ArrayList<String[]> list = new ArrayList<>();
            File file = new File(filename);

            Scanner reader = new Scanner(file);
            String[] data;
            while (reader.hasNextLine()) {
                data = reader.nextLine().split("@@@");
                list.add(data);
            }
            reader.close();

            return list.toArray(new String[list.size()][list.get(0).length]);

        } catch (FileNotFoundException e) {
            System.err.println("No such file");
            return null;
        }
    }

    public static class SimpleServerProtocol {
        private final String[][] dataArray;

        public SimpleServerProtocol(String[][] dataArray) {
            this.dataArray = dataArray;
        }

        public String processInput(String command) {
            if (command != null) {
                String[] data = command.split(";", 2);
                if (data.length != 2) {
                    System.err.println("Wrong command format.");
                    return null;
                }
                String[] types = data[0].split(",");
                String regex = data[1];
                Pattern pattern = Pattern.compile(regex);

                StringBuilder toSend = new StringBuilder();
                for (int i = 0; i < this.dataArray.length; i++) {
                    if (types.length == 0) {
                        Matcher matcher = pattern.matcher(this.dataArray[i][1]);
                        if (matcher.find()) {
                            toSend.append(this.dataArray[i][0]).append("@@@").append(this.dataArray[i][1]).append("\n");
                        }
                    } else {
                        for (String type : types) {
                            if (this.dataArray[i][0].equals(type)) {
                                Matcher matcher = pattern.matcher(this.dataArray[i][1]);
                                if (matcher.find()) {
                                    toSend.append(this.dataArray[i][0]).append("@@@").append(this.dataArray[i][1]).append("\n");
                                    break;
                                }
                            }
                        }
                    }
                }
                return toSend.toString();
            } else {
                return null;
            }
        }
    }
}