package rawsocket;

import hotelapp.Review;
import hotelapp.ThreadSafeHotelData;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

/**
 * A HttpHandler if client send /reviews request.
 */
public class ReviewsHandler implements HttpHandler {
    private ThreadSafeHotelData hdata;

    /**
     * Constructor for class ReviewsHandler.
     * @param hdata
     */
    public ReviewsHandler(ThreadSafeHotelData hdata) {
        this.hdata = hdata;
    }

    /**
     * Override the doGet method to process the GET request.
     * Send the hotel reviews result in JSON format back to client.
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doGet(HttpRawSocketRequest request, HttpRawSocketResponse response) throws IOException {
        StringBuffer sb = new StringBuffer();
        response.setContentType("application/json");
        response.setStatus(RawSocketsHttpServer.SC_OK);
        sb.append("{").append(System.lineSeparator());

        String hotelId, num;
        if ((hotelId = request.getParameter("hotelId")) != null
                && this.hdata.getHotels().contains(hotelId)
                && (num = request.getParameter("num")) != null
                && RawSocketsHttpServer.isInteger(num)) {
            int times = Integer.parseInt(num);
            Set<Review> reviews = this.hdata.getHotelReviews(hotelId);

            if (reviews != null) {
                sb.append("\"success\" : true,").append(System.lineSeparator());
                sb.append("\"hotelId\": \"" + hotelId + "\",").append(System.lineSeparator());
                sb.append("\"reviews\": [").append(System.lineSeparator());

                int i = 1;
                for (Review r : reviews) {
                    if (i > times) {
                        break;
                    }
                    // process the data into JSON
                    sb.append("   {").append(System.lineSeparator());
                    sb.append("   \"reviewId\": \"" + r.getReviewId() + "\",").append(System.lineSeparator());
                    sb.append("   \"title\": \"" + r.getReviewTitle().replaceAll("\\\"", "\\\\\"") + "\",").append(System.lineSeparator());
                    sb.append("   \"user\": \"" + ((r.getUsername().isEmpty()) ? "Anonymous" : r.getUsername()) + "\",").append(System.lineSeparator());
                    sb.append("   \"reviewText\": \"" + r.getReview().replaceAll("\\\"", "\\\\\"") + "\",").append(System.lineSeparator());
                    sb.append("   \"date\": \"" + r.getDate() + "\"").append(System.lineSeparator());

                    if (i == times || i == reviews.size()) {
                        // already print all the result that request by client.
                        sb.append("   }").append(System.lineSeparator());
                        break;
                    } else {
                        sb.append("   },").append(System.lineSeparator());
                    }
                    i++;
                }

                sb.append("]").append(System.lineSeparator());
            }
            else {
                sb.append("\"success\" : false,").append(System.lineSeparator());
                sb.append("\"hotelId\": \"invalid\"").append(System.lineSeparator());
            }
        }
        else {
            sb.append("\"success\" : false,").append(System.lineSeparator());
            sb.append("\"hotelId\": \"invalid\"").append(System.lineSeparator());
        }

        sb.append("}");

        PrintWriter pw = response.getWriter();
        pw.println(sb.toString());
    }

    /**
     * Override the doPost method to process the POST request.
     * @param request
     * @param response
     */
    @Override
    public void doPost(HttpRawSocketRequest request, HttpRawSocketResponse response) {
        response.setContentType("application/json");
        response.setStatus(RawSocketsHttpServer.SC_METHOD_NOT_ALLOWED);
        PrintWriter pw = response.getWriter();
        pw.println("Html 405 Method Not Allowed");
    }

    /**
     * Override the doHead method to process the HEAD request.
     * @param request
     * @param response
     */
    @Override
    public void doHead(HttpRawSocketRequest request, HttpRawSocketResponse response) {
        response.setContentType("application/json");
        response.setStatus(RawSocketsHttpServer.SC_METHOD_NOT_ALLOWED);
        PrintWriter pw = response.getWriter();
        pw.println("Html 405 Method Not Allowed");
    }
}
