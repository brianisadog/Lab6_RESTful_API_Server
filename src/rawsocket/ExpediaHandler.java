package rawsocket;

import hotelapp.Expedia;
import hotelapp.ExpediaAttractions;
import hotelapp.ThreadSafeHotelData;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * A HttpHandler if client send /expedia request.
 */
public class ExpediaHandler implements HttpHandler {
    private ThreadSafeHotelData hdata;

    /**
     * Constructor for class ExpediaHandler.
     * @param hdata
     */
    public ExpediaHandler(ThreadSafeHotelData hdata) {
        this.hdata = hdata;
    }

    /**
     * Override the doGet method to process the GET request.
     * Send the Expedia attraction information result in JSON format back to client.
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
            Expedia expedia = new Expedia(hdata);
            expedia.fetchAttractions(hotelId);
            List<ExpediaAttractions> list = expedia.getExpediaAttractions();

            // process the data into JSON
            sb.append("\"success\" : true,").append(System.lineSeparator());
            sb.append("\"hotelId\": \"" + hotelId + "\",").append(System.lineSeparator());
            sb.append("\"city\": \"" + hotelDetail[2] + "\",").append(System.lineSeparator());
            sb.append("\"results\": [").append(System.lineSeparator());

            for (int i = 0; i < list.size(); i++) {
                ExpediaAttractions ea = list.get(i);
                sb.append("   {").append(System.lineSeparator());
                sb.append("   \"id\": \"" + ea.getId() + "\",").append(System.lineSeparator());
                sb.append("   \"name\": \"" + ea.getName() + "\",").append(System.lineSeparator());
                sb.append("   \"link\": \"" + ea.getLink() + "\",").append(System.lineSeparator());
                sb.append("   \"rating\": \"" + ea.getRating() + "\",").append(System.lineSeparator());
                sb.append("   \"activityPrice\": \"" + ea.getActivityPrice() + "\",").append(System.lineSeparator());
                sb.append("   \"description\": \"" + ea.getDescription() + "\",").append(System.lineSeparator());
                sb.append("   \"activityLocation\": \"" + ea.getActivityLocation() + "\",").append(System.lineSeparator());
                sb.append("   \"meetingOrRedemptionPoint\": [").append(System.lineSeparator());

                int j = 0;
                List<String> address = ea.getMeetingOrRedemptionPoint();
                for (String s : address) {
                    sb.append("       {").append(System.lineSeparator());
                    sb.append("       \"address\": \"" + s + "\"").append(System.lineSeparator());
                    if (j == address.size() - 1) {
                        sb.append("       }").append(System.lineSeparator());
                    }
                    else {
                        sb.append("       },").append(System.lineSeparator());
                    }
                    j++;
                }

                sb.append("   ]").append(System.lineSeparator());

                if (i == list.size() - 1) {
                    sb.append("   }").append(System.lineSeparator());
                }
                else {
                    sb.append("   },").append(System.lineSeparator());
                }
            }

            sb.append("]").append(System.lineSeparator());
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
