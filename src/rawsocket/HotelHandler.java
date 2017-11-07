package rawsocket;

import hotelapp.ThreadSafeHotelData;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * A HttpHandler if client send /hotelInfo request.
 */
public class HotelHandler implements HttpHandler {
    private ThreadSafeHotelData hdata;

    /**
     * Constructor for class HotelHandler.
     * @param hdata
     */
    public HotelHandler(ThreadSafeHotelData hdata) {
        this.hdata = hdata;
    }

    /**
     * Override the doGet method to process the GET request.
     * Send the hotel information result in JSON format back to client.
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

        String hotelId;
        if ((hotelId = request.getParameter("hotelId")) != null
                && this.hdata.getHotels().contains(hotelId)) {
            String[] hotelDetail = this.hdata.getHotelDetail(hotelId);

            // process the data into JSON
            sb.append("\"success\" : true,").append(System.lineSeparator());
            sb.append("\"hotelId\": \"" + hotelId + "\",").append(System.lineSeparator());
            sb.append("\"name\": \"" + hotelDetail[0] + "\",").append(System.lineSeparator());
            sb.append("\"addr\": \"" + hotelDetail[1] + "\",").append(System.lineSeparator());
            sb.append("\"city\": \"" + hotelDetail[2] + "\",").append(System.lineSeparator());
            sb.append("\"state\": \"" + hotelDetail[3] + "\",").append(System.lineSeparator());
            sb.append("\"lat\": \"" + hotelDetail[4] + "\",").append(System.lineSeparator());
            sb.append("\"lng\": \"" + hotelDetail[5] + "\"").append(System.lineSeparator());
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
