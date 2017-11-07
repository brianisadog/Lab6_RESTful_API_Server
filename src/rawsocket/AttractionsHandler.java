package rawsocket;

import hotelapp.Attractions;
import hotelapp.ThreadSafeHotelData;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * A HttpHandler if client send /attractions request.
 */
public class AttractionsHandler implements HttpHandler {
    private ThreadSafeHotelData hdata;

    /**
     * Constructor for class AttractionsHandler.
     * @param hdata
     */
    public AttractionsHandler(ThreadSafeHotelData hdata) {
        this.hdata = hdata;
    }

    /**
     * Override the doGet method to process the GET request.
     * Send the attractions result in JSON format back to client.
     * @param request
     * @param response
     * @throws IOException
     */
    @Override
    public void doGet(HttpRawSocketRequest request, HttpRawSocketResponse response) throws IOException {
        StringBuffer sb = new StringBuffer();
        response.setContentType("application/json");
        response.setStatus(RawSocketsHttpServer.SC_OK);

        String hotelId, radius;
        if (request != null
                && (hotelId = request.getParameter("hotelId")) != null
                && (radius = request.getParameter("radius")) != null
                && this.hdata.getHotels().contains(hotelId)
                && RawSocketsHttpServer.isInteger(radius)) {

            // calling Google Place API to get the attractions result
            Attractions attraction = new Attractions(this.hdata);
            int meters = (int)(1609.344 * Integer.parseInt(radius));
            String result = attraction.fetchAttractions(hotelId, meters);
            sb.append(result);
        }
        else if (request.getParameter("hotelId") == null) {
            sb.append("{").append(System.lineSeparator());
            sb.append("\"success\" : false,").append(System.lineSeparator());
            sb.append("\"hotelId\": \"invalid\"").append(System.lineSeparator());
            sb.append("}");
        }
        else if (request.getParameter("radius") == null
                || !RawSocketsHttpServer.isInteger(request.getParameter("radius"))) {
            sb.append("{").append(System.lineSeparator());
            sb.append("\"success\" : false,").append(System.lineSeparator());
            sb.append("\"radius\": \"invalid\"").append(System.lineSeparator());
            sb.append("}");
        }
        else {
            sb.append("{").append(System.lineSeparator());
            sb.append("\"success\" : false,").append(System.lineSeparator());
            sb.append("\"hotelId\": \"invalid\"").append(System.lineSeparator());
            sb.append("}");
        }

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
