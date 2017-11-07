package rawsocket;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * A data structure to store the detail and PrintWriter of response.
 * @author Brian Sung
 */
public class HttpRawSocketResponse {
    private String contentType;
    private String status;
    private PrintWriter pw;

    /**
     * Constructor for class HttpRawSocketResponse.
     * To initialize the PrintWriter in order to send response back to client.
     * @param outputStream
     * @throws IOException
     */
    public HttpRawSocketResponse(OutputStream outputStream) throws IOException {
        this.pw = new PrintWriter(outputStream, true);
    }

    /**
     * To set the content type of the response.
     * @param contentType
     */
    public void setContentType(String contentType) {
        this.contentType = "Content-Type: " + contentType;
    }

    /**
     * To set the response status.
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Return the PrintWriter for HttpHandler to print the result.
     * @return PrintWriter
     */
    public PrintWriter getWriter() {
        pw.println(status);
        pw.println(contentType);
        pw.println();
        return this.pw;
    }
}
