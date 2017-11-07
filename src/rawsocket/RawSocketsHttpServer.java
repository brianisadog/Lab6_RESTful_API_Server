package rawsocket;

import hotelapp.*;
import java.nio.file.Paths;
import java.util.concurrent.*;
import java.net.*;
import java.io.*;

/**
 * RESTful API server implement by using Socket.
 *
 * @author Brian Sung
 */
public class RawSocketsHttpServer extends Thread {
    private final int PORT = 3000;
    private boolean alive = true; // whether the server is alive or shutdown
    private static HotelDataBuilder builder;
    private static ThreadSafeHotelData hdata;
    public static final int THREAD = 4; // multi-threads to load the hotelInfo and reviews
    public static final String SC_OK = "HTTP/1.1 200 OK";
    public static final String SC_BAD_REQUEST = "HTTP/1.1 400 Bad Request";
    public static final String SC_NOT_FOUND = "HTTP/1.1 404 Not Found";
    public static final String SC_METHOD_NOT_ALLOWED = "HTTP/1.1 405 Method Not Allowed";

    /**
     * Server starter,load all hotel information and reviews into ThreadSafeHotelData before start.
     * @param args
     */
    public static void main(String[] args) {
        // before server start, load all hotel info and reviews into data structure
        hdata = new ThreadSafeHotelData();
        builder = new HotelDataBuilder(hdata, THREAD);
        String inputHotelFile = "input" + File.separator + "hotels.json";
        builder.loadHotelInfo(inputHotelFile);
        builder.loadReviews(Paths.get("input" + File.separator + "reviews"));

        // create handler and sockets and mapping to path, passing hdata to make instance variable secured
        RawSocketHandler handler = new RawSocketHandler();
        handler.addSocketClassMapping("hotelInfo", new HotelHandler(hdata));
        handler.addSocketClassMapping("reviews", new ReviewsHandler(hdata));
        handler.addSocketClassMapping("attractions", new AttractionsHandler(hdata));
        handler.addSocketClassMapping("expedia", new ExpediaHandler(hdata));

        // start server
        new RawSocketsHttpServer().startServer(handler);
    }

    /**
     * WelcomeSocket to connect with clients.
     * Submit new client request into thread pool to assign a thread to process
     * a client request, multi-threading allows clients to send request in the send time.
     */
    public void startServer(RawSocketHandler handler) {
        final ExecutorService threads = Executors.newFixedThreadPool(THREAD);

        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket welcomingSocket = new ServerSocket(PORT);
                    System.out.println("Waiting for clients to connect...");
                    while (alive) {
                        Socket clientSocket = welcomingSocket.accept();
                        threads.submit(new ClientTask(clientSocket, handler));
                    }
                    if (!alive) {
                        welcomingSocket.close();
                    }
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                }
            }
        };

        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }

    /**
     * Inner class that implements Runnable for ExecutorService to execute.
     *
     * run() method to use multi-threading to process client request simultaneously.
     */
    private class ClientTask implements Runnable {
        private final Socket connectionSocket;
        private final RawSocketHandler handler;

        /**
         * Constructor for class ClientTask.
         * @param connectionSocket
         */
        private ClientTask(Socket connectionSocket, RawSocketHandler handler) {
            this.connectionSocket = connectionSocket;
            this.handler = handler;
        }

        /**
         * Allows thread to process request by client.
         */
        @Override
        public void run() {
            System.out.println("A client connected.");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()))) {

                // get the request
                StringBuffer input = new StringBuffer();
                String line = reader.readLine();
                while (line!= null && !line.isEmpty()) {
                    input.append(line).append(System.lineSeparator());
                    line = reader.readLine();
                }
                System.out.println("Request: " + System.lineSeparator() + input.toString());

                // create request and response objects like servlet
                HttpRawSocketResponse response = new HttpRawSocketResponse(connectionSocket.getOutputStream());
                HttpRawSocketRequest request = new HttpRawSocketRequest(input.toString());

                // parse request headers
                request.parsingHeader();

                // start do...
                handler.startSocket(request, response);
            }
            catch (IOException e) {
                System.out.println(e);
            }
            finally {
                try {
                    if (connectionSocket != null)
                        connectionSocket.close();
                }
                catch (IOException e) {
                    System.out.println("Can't close the socket : " + e);
                }
            }
        }
    }

    /**
     * Check if string is an integer or not.
     * @param s
     * @return boolean
     */
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
}
