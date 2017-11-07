package jetty;

import hotelapp.Expedia;
import hotelapp.ExpediaAttractions;
import hotelapp.ThreadSafeHotelData;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * A Servlet handler if client send /expedia request.
 */
public class ExpediaServlet extends HttpServlet {
    private ThreadSafeHotelData hdata;

    /**
     * Constructor for class ExpediaServlet.
     * @param hdata
     */
    public ExpediaServlet(ThreadSafeHotelData hdata) {
        this.hdata = hdata;
    }

    /**
     * Override the doGet method to process the GET request.
     * Send the Expedia attraction information result in JSON format back to client.
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

        String hotelId;
        if (request != null
                && StringEscapeUtils.escapeHtml4(hotelId = request.getParameter("hotelId")) != null
                && this.hdata.getHotels().contains(hotelId)) {
            String[] hotelDetail = this.hdata.getHotelDetail(hotelId);
            Expedia expedia = new Expedia(hdata);
            expedia.fetchAttractions(hotelId);
            List<ExpediaAttractions> list = expedia.getExpediaAttractions();

            // process the data into JSON
            out.println("\"success\" : true,");
            out.println("\"hotelId\": \"" + hotelId + "\",");
            out.println("\"city\": \"" + hotelDetail[2] + "\",");
            out.println("\"results\": [");

            for (int i = 0; i < list.size(); i++) {
                ExpediaAttractions ea = list.get(i);
                out.println("   {");
                out.println("   \"id\": \"" + ea.getId() + "\",");
                out.println("   \"name\": \"" + ea.getName() + "\",");
                out.println("   \"link\": \"" + ea.getLink() + "\",");
                out.println("   \"rating\": \"" + ea.getRating() + "\",");
                out.println("   \"activityPrice\": \"" + ea.getActivityPrice() + "\",");
                out.println("   \"description\": \"" + ea.getDescription() + "\",");
                out.println("   \"activityLocation\": \"" + ea.getActivityLocation() + "\",");
                out.println("   \"meetingOrRedemptionPoint\": [");

                int j = 0;
                List<String> address = ea.getMeetingOrRedemptionPoint();
                for (String s : address) {
                    out.println("       {");
                    out.println("       \"address\": \"" + s + "\"");
                    if (j == address.size() - 1) {
                        out.println("       }");
                    }
                    else {
                        out.println("       },");
                    }
                    j++;
                }

                out.println("   ]");

                if (i == list.size() - 1) {
                    out.println("   }");
                }
                else {
                    out.println("   },");
                }
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
