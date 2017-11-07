package jetty;

import hotelapp.Review;
import hotelapp.ThreadSafeHotelData;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

/**
 * A Servlet handler if client send /reviews request.
 */
public class ReviewsServlet extends HttpServlet {
    private ThreadSafeHotelData hdata;

    /**
     * Constructor for class ReviewsServlet.
     * @param hdata
     */
    public ReviewsServlet(ThreadSafeHotelData hdata) {
        this.hdata = hdata;
    }

    /**
     * Override the doGet method to process the GET request.
     * Send the hotel reviews result in JSON format back to client.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("A client connected.");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();
        out.println("{");

        String hotelId, reviewsNum;
        if (request != null
                && StringEscapeUtils.escapeHtml4(hotelId = request.getParameter("hotelId")) != null
                && StringEscapeUtils.escapeHtml4(reviewsNum = request.getParameter("num")) != null
                && this.hdata.getHotels().contains(hotelId)
                && JettyHttpServer.isInteger(reviewsNum)) {
            int num = Integer.parseInt(reviewsNum);
            Set<Review> reviews = this.hdata.getHotelReviews(hotelId);

            out.println("\"success\" : true,");
            out.println("\"hotelId\": \"" + hotelId + "\",");
            out.println("\"reviews\": [");

            int i = 1;
            for (Review r : reviews) {
                if (i > num) {
                    break;
                }
                // process the data into JSON
                out.println("   {");
                out.println("   \"reviewId\": \"" + r.getReviewId() + "\",");
                out.println("   \"title\": \"" + r.getReviewTitle().replaceAll("\\\"", "\\\\\"") + "\",");
                out.println("   \"user\": \"" + ((r.getUsername().isEmpty()) ? "Anonymous" : r.getUsername()) + "\",");
                out.println("   \"reviewText\": \"" + r.getReview().replaceAll("\\\"", "\\\\\"") + "\",");
                out.println("   \"date\": \"" + r.getDate() + "\"");
                if (i == num || i == reviews.size()) {
                    // already print all the result that request by client.
                    out.println("   }");
                    break;
                }
                else {
                    out.println("   },");
                }
                i++;
            }

            out.println("]");
        }
        else {
            out.println("\"success\" : false,");
            out.println("\"hotelId\": \"invalid\"");
        }
        out.println("}");
    }
}
