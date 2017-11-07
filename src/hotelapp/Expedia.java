package hotelapp;

import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class Expedia.
 * To scrape information from Expedia.
 *
 * @author Brian Sung
 */
public class Expedia {
    private final String HOST = "www.expedia.com";
    private final String PATH = "/things-to-do/";
    private final int PORT = 443;
    private ThreadSafeHotelData hdata;
    private Set<String> links;

    /**
     * Constructor for class Expedia.
     *
     * @param hdata
     */
    public Expedia(ThreadSafeHotelData hdata) {
        this.hdata = hdata;
        this.links = new HashSet<>();
    }

    /**
     * Get the links of attractions from searching on Expedia
     * , store them into data structure. Then go to the link
     * to get the detail of the attractions.
     */
    public void fetchAttractions(String hotelId) throws IOException {
        // create the query for searching
        String[] hotelDetail = hdata.getHotelDetail(hotelId);
        StringBuilder Query = new StringBuilder();
        Query.append("search?location=");
        Query.append(hotelDetail[2].replaceAll(" ", "+"));

        // store the flex-link we found by searching on Expedia into data structure
        storeLinks(getHtmlResponse(Query.toString(), "links"));
    }

    /**
     * To get the list of attractions object we get from Expedia.
     *
     * @return List of ExpediaAttractions
     * @throws IOException
     */
    public List<ExpediaAttractions> getExpediaAttractions() throws IOException {
        List<ExpediaAttractions> attractions = new ArrayList<>();

        for (String link : this.links) {
            String detail = getHtmlResponse(link, "detail");
            ExpediaAttractions ea = createAttraction(detail, link);
            attractions.add(ea);
        }

        return attractions;
    }

    /**
     * Creates a secure socket to communicate with Expedia.
     *
     * @return String
     */
    private String getHtmlResponse(String query, String type) throws IOException {
        // connect with host -Expedia
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket socket = factory.createSocket(HOST, PORT);

        // send a request to the server through output stream
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        String requestString = getRequest(HOST, PATH + query);
        pw.println(requestString);

        // use input stream to read server's response
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder buf = new StringBuilder();
        String str;
        if (type.equals("links")) {
            while ((str = in.readLine()) != null) {
                // just store the info we need
                if (str.contains("class=\"flex-link\"")) {
                    buf.append(str);
                }
            }
        }
        else if (type.equals("detail")) {
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            // take the body part
            return buf.substring(buf.indexOf("<body"));
        }

        return buf.toString();
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

    /**
     * To store the attraction links into data structure.
     *
     * @param htmlString
     */
    private void storeLinks(String htmlString) {
        Pattern p = Pattern.compile("<a class=\"flex-link\" href=\"([^\"]+)\"[^>]+>");
        Matcher m = p.matcher(htmlString);
        while (m.find()) {
            // using hash set to avoid duplicate
            links.add(m.group(1));
        }
    }

    /**
     * To create an attraction object from the link.
     *
     * @param htmlString
     * @param link
     * @return ExpediaAttractions
     */
    private ExpediaAttractions createAttraction(String htmlString, String link) {
        String id = null;
        String name = null;
        double rating = 0;
        double activityPrice = 0;
        String description = null;
        String activityLocation = null;
        String meetingOrRedemptionPoint = null;

        // id in the link could look like this:
        // hop-on-hop-off-bus-tour.a192698.activity-details?srp=true&location=San+Francisco
        // "a192698" -> id = "activity192698"
        Pattern idP = Pattern.compile(".*?\\.a(.*?)\\..*");
        Matcher idM = idP.matcher(link);
        if (idM.find()) {
            id = "activity" + idM.group(1);
        }

        // get information from body
        StringBuilder sb = new StringBuilder();
        sb.append(".*?id=\"activityTitle\"[^>]*>([^<]+)"); // attraction name
        sb.append(".*?id=\"activityFromPrice\"[^>]*>\\$([^<]+)"); // price
        sb.append(".*?class=\"details-review-score bold\">\\s*([^/]+)"); // rating
        sb.append(".*?id=\"expandedDescription\"[^>]*>((\\s?<p>[^<]*</p>)+)"); // description
        sb.append(".*?Activity Location.*?((\\s?<p class=\"point-address\">[^<]+</p>)+)"); // activity location
        sb.append(".*?Meeting/Redemption Point.*?((\\s?<li>\\s(\\s?<p class=\"point-address\">[^<]+</p>)+\\s</li>)+)"); //meeting/redemption point
        Pattern p = Pattern.compile(sb.toString());
        Matcher m = p.matcher(htmlString);
        if (m.find()) {
            name = m.group(1);
            rating = Double.parseDouble(m.group(3));
            activityPrice = Double.parseDouble(m.group(2));
            description = m.group(4).replaceAll("(<p>)|(</p>)", "");

            // parsing activity location
            StringBuilder alSB = new StringBuilder();
            Pattern alP = Pattern.compile("<p.*?>([^<]+)</p>");
            Matcher alM = alP.matcher(m.group(6));
            while (alM.find()) {
                alSB.append(",").append(alM.group(1));
            }
            activityLocation = alSB.substring(1);

            meetingOrRedemptionPoint = m.group(8);
        }

        ExpediaAttractions ea
                = new ExpediaAttractions(id, name, link, rating, activityPrice, description, activityLocation);

        // parsing meeting/redemption point
        if (meetingOrRedemptionPoint != null) {
            Pattern pForLi = Pattern.compile("<li>(.*?)</li>");
            Matcher mForLi = pForLi.matcher(meetingOrRedemptionPoint);
            Pattern mpP = Pattern.compile("<p.*?>([^<]+)</p>");
            while (mForLi.find()) {
                StringBuilder mpSB = new StringBuilder();
                Matcher mpM = mpP.matcher(mForLi.group(1));
                while (mpM.find()) {
                    mpSB.append(mpM.group(1) + (mpSB.length() == 0 ? ": " : ","));
                }
                if (mpSB.length() > 0) {
                    ea.addMeetingOrRedemptionPoint(mpSB.substring(0, mpSB.length() - 1));
                }
            }
        }

        return ea;
    }
}
