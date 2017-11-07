package rawsocket;

import concurrent.ReentrantReadWriteLock;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * A class to store HttpHandler and to determine
 * which handler and which request type to use.
 * @author Brian Sung
 */
public class RawSocketHandler {
    private Map<String, HttpHandler> handlers;

    /**
     * Constructor for class RawSocketHandler.
     */
    public RawSocketHandler() {
        this.handlers = new HashMap<>();
    }

    /**
     * To map the source path with HttpHandler class.
     * @param path
     * @param _class
     */
    public void addSocketClassMapping(String path, HttpHandler _class) {
        handlers.put(path, _class);
    }

    /**
     * To determine which handler and which request type to use.
     * @param request
     * @param response
     * @throws IOException
     */
    public void startSocket(HttpRawSocketRequest request, HttpRawSocketResponse response) throws IOException {
        String path = request.getPath();
        String requestType = request.getRequestType();

        if (path != null && requestType != null && handlers.containsKey(path)) {
            if (requestType.equals("GET")) {
                handlers.get(path).doGet(request, response);
            }
            else if (requestType.equals("POST")) {
                handlers.get(path).doPost(request, response);
            }
            else if (requestType.equals("HEAD")) {
                handlers.get(path).doHead(request, response);
            }
            else {
                // cannot handle such request type
                response.setContentType("application/json");
                response.setStatus(RawSocketsHttpServer.SC_BAD_REQUEST);
                PrintWriter pw = response.getWriter();
                pw.println("Html 400 Bad Request");
            }
        }
        else if (!path.isEmpty() && !handlers.containsKey(path)) {
            // no such method
            response.setContentType("application/json");
            response.setStatus(RawSocketsHttpServer.SC_NOT_FOUND);
            PrintWriter pw = response.getWriter();
            pw.println("Html 404 Not Found");
        }
        else {
            // need path
            response.setContentType("application/json");
            response.setStatus(RawSocketsHttpServer.SC_METHOD_NOT_ALLOWED);
            PrintWriter pw = response.getWriter();
            pw.println("Html 405 Method Not Allowed");
        }
    }
}
