package rawsocket;

import java.io.IOException;

/**
 * A HttpHandler interface to handle request sent by client.
 */
public interface HttpHandler {
    /**
     * doGet method to handle GET request.
     * @param request
     * @param response
     * @throws IOException
     */
    public void doGet(HttpRawSocketRequest request, HttpRawSocketResponse response) throws IOException;

    /**
     * doPost method to handle POST request.
     * @param request
     * @param response
     */
    public void doPost(HttpRawSocketRequest request, HttpRawSocketResponse response);

    /**
     * doHead method to handle HEAD request.
     * @param request
     * @param response
     */
    public void doHead(HttpRawSocketRequest request, HttpRawSocketResponse response);
}
