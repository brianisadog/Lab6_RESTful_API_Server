package jetty;

import hotelapp.HotelDataBuilder;
import hotelapp.ThreadSafeHotelData;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * RESTful API server implement by using Jetty and Servlet.
 *
 * @author Brian Sung
 */
public class JettyHttpServer {
    private static final int PORT = 3050;
    private static HotelDataBuilder builder;
    private static ThreadSafeHotelData hdata;
    public static final int THREAD = 4; // multi-threads to load the hotelInfo and reviews

    /**
     * Server starter,load all hotel information and reviews into ThreadSafeHotelData before start.
     * @param args
     */
    public static void main(String[] args) {
        // before server start, load all hotel info and reviews into data structure
        hdata = new ThreadSafeHotelData();
        builder = new HotelDataBuilder(hdata, THREAD);
        String inputHotelFile = "input" + File.separator + "hotels.json";
        builder.loadHotelInfo(inputHotelFile);
        builder.loadReviews(Paths.get("input" + File.separator + "reviews"));

        // servlet setting
        Server server = new Server(PORT);
        ServletHandler handler = new ServletHandler();

        // use ServletHolder to make instance variable secured
        handler.addServletWithMapping(new ServletHolder(new HotelInfoServlet(hdata)), "/hotelInfo");
        handler.addServletWithMapping(new ServletHolder(new ReviewsServlet(hdata)), "/reviews");
        handler.addServletWithMapping(new ServletHolder(new AttractionsServlet(hdata)), "/attractions");
        handler.addServletWithMapping(new ServletHolder(new ExpediaServlet(hdata)), "/expedia");

        try {
            server.setHandler(handler);
            server.start();
            server.join();
        }
        catch (ServletException e) {
            System.out.println("ServletException happened: " + e);
        }
        catch (IOException e) {
            System.out.println("IOException happened: " + e);
        }
        catch (Exception e) {
            System.out.println("Exception happened: " + e);
        }
    }

    /**
     * Check if string is an integer or not.
     * @param s
     * @return boolean
     */
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
}
