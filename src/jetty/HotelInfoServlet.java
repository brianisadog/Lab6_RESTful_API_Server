package jetty;

import hotelapp.ThreadSafeHotelData;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A Servlet handler if client send /hotelInfo request.
 */
public class HotelInfoServlet extends HttpServlet {
    private ThreadSafeHotelData hdata;

    /**
     * Constructor for class HotelInfoServlet.
     * @param hdata
     */
    public HotelInfoServlet(ThreadSafeHotelData hdata) {
        this.hdata = hdata;
    }

    /**
     * Override the doGet method to process the GET request.
     * Send the hotel information result in JSON format back to client.
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

            // process the data into JSON
            out.println("\"success\" : true,");
            out.println("\"hotelId\": \"" + hotelId + "\",");
            out.println("\"name\": \"" + hotelDetail[0] + "\",");
            out.println("\"addr\": \"" + hotelDetail[1] + "\",");
            out.println("\"city\": \"" + hotelDetail[2] + "\",");
            out.println("\"state\": \"" + hotelDetail[3] + "\",");
            out.println("\"lat\": \"" + hotelDetail[4] + "\",");
            out.println("\"lng\": \"" + hotelDetail[5] + "\"");
        }
        else {
            out.println("\"success\" : false,");
            out.println("\"hotelId\": \"invalid\"");
        }

        out.println("}");
    }

}
