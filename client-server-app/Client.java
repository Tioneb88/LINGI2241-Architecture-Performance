/*
 * LINGI2241 - Architecture Performance and Computer Systems
 * Project : Measurement & Modeling 
 * Author : Benoît Michel - 23971600
 * Date : december 2020
 * Based on https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
 */


import java.util.*;
import java.net.*;
import java.io.*;

/*
 * Client able to send requests to the server and treat responses.
 */
public class Client {

    static Random seed;

    /*
     * Main method to launch the client.
     * @param inputFilename : file from where to read the requests
     * @param hostName : name of the server to use
     * @param portNumber : the port used to establish the connection with the server
     * @param nbClients : the number of clients wanted for the server
     * @param meanDelay : delay for the exponential distribution
     * @param resultFilename : the name for the file with all the results
     * @return None
     */
    public static void main(String[] args) {
        // seed for the exponential distribution
        seed = new Random();

        // Checking of the usage
        if (args.length != 6) {
            System.err.println("Usage: java Client <input filename> <host name> <port number> <number of clients> <mean delay> <result filename>");
            System.exit(1);
        }

        // Arguments recovery and client creation
        List<String> requests = fileToList(args[0]);
        final List<Long> results = new ArrayList<>();
        int nbClients = Integer.parseInt(args[3]);
        float lambda = 1/Float.parseFloat(args[4]);
        String outputFilename = args[5];

        // Definition and execution of the client threads
        Thread[] threads = new Thread[nbClients];
        try (
            // client read and write for the requests
            final Socket clientSocket = new Socket(args[1], Integer.parseInt(args[2]));
            final BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            final PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {
            // Definition of the sending threads
            for (int i = 0; i<nbClients; i++) {
                threads[i] = new Thread(() -> {
                    try {
                        Thread thread = new Thread(() -> {
                            List<String> threadRequests = new ArrayList<>(requests);
                            Collections.shuffle(threadRequests);

                            for (String request : threadRequests) {
                                try {
                                    // simulate inter-arrival time
                                    Thread.sleep((long) exponential(lambda));

                                    request = new Date().getTime() + ";" + request;

                                    synchronized (writer) {
                                        writer.println(request);
                                    }

                                } catch (InterruptedException e){
                                    System.err.println(e.getMessage());
                                }
                            }
                        });
                        thread.start();
                        thread.join();

                    } catch (InterruptedException e) {
                        System.err.println(e.getMessage());
                    }
                });
                threads[i].start();
            }

            // Definition of the receiving threads
            Thread thread = new Thread(() -> {
                try {
                    String line = reader.readLine();
                    int count = 0;
                    boolean resp = true;

                    while (line != null) {

                        if (resp) {
                            resp = false;
                            String[] splitResponse = line.split(";", 2);
                            results.add(new Date().getTime() - Long.parseLong(splitResponse[0]));
                            line = splitResponse[1];
                        }

                        if (line.equals("")) {
                            resp = true;
                            count++;
                        }

                        if (count == requests.size() * nbClients)
                            break;

                        line = reader.readLine();
                    }

                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            });
            thread.start();

            for (Thread t : threads) {
                t.join();
            }

            thread.join();

        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        saveResults(results, outputFilename + ".txt");

        System.out.println("All clients finished !");
    }


    /*
     * Reads the file and adds all the lines to a list of strings.
     * @param filename : the file from which to read the lines
     * @return list : a list of the strings (lines) in the file
     */
    public static List<String> fileToList(String filename) {
        try {
            String line;
            List<String> list = new ArrayList<>();
            Scanner cursor = new Scanner(new File(filename));

            while (cursor.hasNext()) {
                if ((line = cursor.nextLine()) != null) ;
                list.add(line);
            }

            cursor.close();
            System.out.println("Lines read from " + filename);
            return list;

        } catch (IOException e) {
            System.err.println("Error with the file to read !");
            return null;
        }
    }

    /*
     * Simulates an exponential distribution.
     * @param lambda : the parameter of the distribution
     * @return time : the time to wait according to the distribution and the seed
     */
    public static double exponential(float lambda) {
        return Math.log(1-seed.nextDouble())/(-lambda);
    }

    /*
     * Saves the results of the list in the file.
     * @param list : list of results
     * @param fileName : the file where to write the results
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

}