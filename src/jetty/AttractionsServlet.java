package jetty;

import hotelapp.Attractions;
import hotelapp.ThreadSafeHotelData;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * A Servlet handler if client send /attractions request.
 */
public class AttractionsServlet extends HttpServlet {
    private ThreadSafeHotelData hdata;

    /**
     * Constructor for class AttractionsServlet.
     * @param hdata
     */
    public AttractionsServlet(ThreadSafeHotelData hdata) {
        this.hdata = hdata;
    }

    /**
     * Override the doGet method to process the GET request.
     * Send the attractions result in JSON format back to client.
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

        String hotelId, radius;
        if (request != null
                && StringEscapeUtils.escapeHtml4(hotelId = request.getParameter("hotelId")) != null
                && StringEscapeUtils.escapeHtml4(radius = request.getParameter("radius")) != null
                && this.hdata.getHotels().contains(hotelId)
                && JettyHttpServer.isInteger(radius)) {

            // calling Google Place API to get the attractions result
            Attractions attraction = new Attractions(this.hdata);
            int meters = (int)(1609.344 * Integer.parseInt(radius));
            String result = attraction.fetchAttractions(hotelId, meters);
            out.println(result);
        }
        else if (request.getParameter("hotelId") == null) {
            out.println("{");
            out.println("\"success\" : false,");
            out.println("\"hotelId\": \"invalid\"");
            out.println("}");
        }
        else if (request.getParameter("radius") == null
                || !JettyHttpServer.isInteger(request.getParameter("radius"))) {
            out.println("{");
            out.println("\"success\" : false,");
            out.println("\"radius\": \"invalid\"");
            out.println("}");
        }
        else {
            out.println("{");
            out.println("\"success\" : false,");
            out.println("\"hotelId\": \"invalid\"");
            out.println("}");
        }
    }
}
