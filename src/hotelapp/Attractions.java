package hotelapp;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;

/**
 * Class Attractions.
 * @author Brian Sung
 */
public class Attractions {
    private final String HOST = "maps.googleapis.com";
    private final String PATH = "/maps/api/place/textsearch/json";
    private final String KEY = "AIzaSyBcvV5tPZBJSfXxnuhlnzCs8Rs0lXY8N0A";
    private final int PORT = 443;
    private ThreadSafeHotelData hdata;

    /** Constructor for TouristAttractionFinder
     *
     * @param hdata
     */
    public Attractions(ThreadSafeHotelData hdata) {
        this.hdata = hdata;
    }

    /**
     * Creates a secure socket to communicate with Google Place API server that
     * provides Places API, sends a GET request (to find attractions close to
     * the hotel within a given radius), and gets a response as a string.
     *
     * @return String
     */
    public String fetchAttractions(String hotelId, int radius) throws IOException {
        String[] hotelDetail = hdata.getHotelDetail(hotelId);
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket socket = factory.createSocket(HOST, PORT);
        StringBuffer buf = new StringBuffer();

        // output stream for the secure socket
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        StringBuffer pathAndQuery = new StringBuffer();
        pathAndQuery.append(PATH);
        pathAndQuery.append("?");
        pathAndQuery.append("query=tourist%20attractions+in+");
        pathAndQuery.append(hotelDetail[2].replaceAll(" ", "%20"));
        pathAndQuery.append("&location=").append(hotelDetail[4]);
        pathAndQuery.append(",").append(hotelDetail[5]);
        pathAndQuery.append("&radius=").append(radius);
        pathAndQuery.append("&language=en");
        pathAndQuery.append("&key=").append(KEY);
        String requestString = getRequest(HOST, pathAndQuery.toString());

        pw.println(requestString); // send a request to the server

        // input stream for the secure socket.
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // use input stream to read server's response
        String str;
        while ((str = in.readLine()) != null) {
            buf.append(str);
        }

        // return only the JSON part
        String htmlResult = buf.toString();
        return htmlResult.substring(htmlResult.indexOf("{"));
    }

    /**
     * A method that creates a GET request for the given host and resource.
     *
     * @param host
     * @param pathResourceQuery
     * @return String
     *          - HTTP GET request returned as a string
     */
    private String getRequest(String host, String pathResourceQuery) {
        String request = "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator() // GET
                // request
                + "Host: " + host + System.lineSeparator() // Host header required for HTTP/1.1
                + "Connection: close" + System.lineSeparator() // make sure the server closes the
                // connection after we fetch one page
                + System.lineSeparator();
        return request;
    }
}
