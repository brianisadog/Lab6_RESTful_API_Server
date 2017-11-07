package rawsocket;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A data structure to store and process request sent by client.
 * @author Brian Sung
 */
public class HttpRawSocketRequest {
    private String request;
    private String requestType;
    private String path;
    private Map<String, String> headers;
    private Map<String, String> parameterMap;

    /**
     * Constructor for class HttpRawSocketRequest.
     * @param request
     */
    public HttpRawSocketRequest(String request) {
        this.request = request;
        headers = new HashMap<>();
        parameterMap = new HashMap<>();
    }

    /**
     * Parsing the headers into data structures.
     *
     * Parsing: Request Methods, Host, Connection, Accept, Accept-Language
     *          , Accept-Encoding, Cookie, Date, Cache-Control, User Agent
     *          , Upgrade, If-Modified-Since......
     */
    public void parsingHeader() {
        // parsing http request headers
        String[] requestLine = request.split(System.lineSeparator());
        for (int i = 1; i < requestLine.length; i++) {
            String[] nameAndValue = requestLine[i].split(":\\s");
            headers.put(nameAndValue[0], nameAndValue[1]);
        }

        /** Parsing Request Methods. */
        String[] requestDetail = requestLine[0].split(" ");

        // get request type
        if (requestDetail.length >= 1) {
            this.requestType = requestDetail[0];
        }

        // get path and load parameters
        if (requestDetail.length >= 2) {
            String[] pathAndQuery = requestDetail[1].split("\\?");
            if (pathAndQuery.length >= 1) {
                this.path = pathAndQuery[0].replace("/", "");
            }
            if (pathAndQuery.length >= 2) {
                Pattern p = Pattern.compile("&?([^\\s\\?&=]+)=([^\\s\\?&=]*)");
                Matcher m = p.matcher(pathAndQuery[1]);

                // load all parameters into HashMap, group1 = parameter name, group2 = value
                while (m.find()) {
                    parameterMap.put(m.group(1), StringEscapeUtils.escapeHtml4(m.group(2)));
                }
            }
        }
    }

    /**
     * Return the type of the request.
     * @return String
     */
    public String getRequestType() {
        return this.requestType;
    }

    /**
     * Return the source path of the request.
     * @return String
     */
    public String getPath() {
        return this.path;
    }

    /**
     * Return the value of the parameter.
     * @param name
     * @return String
     */
    public String getParameter(String name) {
        return this.parameterMap.get(name);
    }

    /**
     * A getter to get header content.
     * @param name
     * @return String
     */
    public String getHeaders(String name) {
        return this.headers.get(name);
    }

    /**
     * The Host request header specifies the domain name of the server
     * , and (optionally) the TCP port number on which the server is listening.
     *
     * @return String
     */
    public String getHost() {
        return getHeaders("Host");
    }

    /**
     * To get the domain or port separately.
     *
     * @return String
     */
    public String getHost(String part) {
        String result = null;
        String host;
        if ((host = getHeaders("Host")) != null) {
            String[] domainAndHost = host.split(":");
            if (part.equals("domain")) {
                result = domainAndHost[0];
            }
            else if (part.equals("port") && domainAndHost.length >= 2) {
                result = domainAndHost[1];
            }
        }
        return result;
    }

    /**
     * The Connection general header controls whether or not the network
     * connection stays open after the current transaction finishes.
     *
     * @return String
     */
    public String getConnection() {
        return getHeaders("Connection");
    }

    /**
     * The Accept request HTTP header advertises which content types
     * , the client is able to understand.
     * Each type splits by ","
     * Number following ";q=" means priority of the type.
     *
     * @return List of String
     */
    public List<String> getAccept() {
        String acceptValues;
        List<String> result = new ArrayList<>();
        if ((acceptValues = getHeaders("Accept")) != null) {
            Pattern p = Pattern.compile(",?\\s?([^,/]+/[^,/;]+)(;q=[^,]+)?");
            Matcher m = p.matcher(acceptValues);
            while (m.find()) {
                result.add(m.group(1)); // ignore the priority
            }
        }
        return result;
    }

    /**
     * The Accept-Language request HTTP header advertises which languages
     * the client is able to understand, and which locale variant is preferred.
     * Each language splits by ","
     * Number following ";q=" means priority of the type.
     *
     * @return List of String
     */
    public List<String> getAcceptLenguage() {
        String acceptLanguageValues;
        List<String> result = new ArrayList<>();
        if ((acceptLanguageValues = getHeaders("Accept-Language")) != null) {
            Pattern p = Pattern.compile(",?\\s?([^,/;]+)(;q=[^,]+)?");
            Matcher m = p.matcher(acceptLanguageValues);
            while (m.find()) {
                result.add(m.group(1)); // ignore the priority
            }
        }
        return result;
    }

    /**
     * The Accept-Encoding request HTTP header advertises which content encoding
     * , usually a compression algorithm, the client is able to understand.
     * Number following ";q=" means priority of the type.
     *
     * @return List of String
     */
    public List<String> getAcceptEncoding() {
        String acceptEncodingValues;
        List<String> result = new ArrayList<>();
        if ((acceptEncodingValues = getHeaders("Accept-Encoding")) != null) {
            Pattern p = Pattern.compile(",?\\s?([^,/;]+)(;q=[^,]+)?");
            Matcher m = p.matcher(acceptEncodingValues);
            while (m.find()) {
                result.add(m.group(1)); // ignore the priority
            }
        }
        return result;
    }

    /**
     * The Cookie HTTP request header contains stored HTTP cookies
     * previously sent by the server with the Set-Cookie header.
     * Each token and value pair splits by ";"
     *
     * @return Mapping the token and the value
     */
    public Map<String, String> getCookie() {
        String cookieValues;
        Map<String, String> result = new HashMap<>();
        if ((cookieValues = getHeaders("Cookie")) != null) {
            String[] pairs = cookieValues.split(";\\s?");
            for (String tokenAndValue : pairs) {
                String[] temp = tokenAndValue.split("=");
                result.put(temp[0], temp[1]);
            }
        }
        return result;
    }

    /**
     * The Date general HTTP header contains the date and time at
     * which the message was originated.
     *
     * @return String
     */
    public String getDate() {
        return getHeaders("Date");
    }

    /**
     * The Cache-Control general-header field is used to specify
     * directives for caching mechanisms in both requests and responses.
     * Each directive splits by ","
     *
     * @return String[]
     */
    public String[] getCacheControl() {
        return getHeaders("Cache-Control").split(",\\s?");
    }

    /**
     * The User-Agent request header contains a characteristic string
     * that allows the network protocol peers to identify the application
     * type, operating system, software vendor or software version of
     * the requesting software user agent.
     * Example - User-Agent: python-requests/2.18.4
     *
     * @return String
     */
    public String getUserAgent() {
        return getHeaders("User-Agent");
    }

    /**
     * To get the domain or port separately in the User-Agent value.
     * Example - Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0
     *      compatible = Mozilla/5.0
     *      platform = (Windows NT 6.1; Win64; x64; rv:47.0)
     *      browser = Gecko/20100101 Firefox/47.0
     *
     * @return String
     */
    public String getUserAgent(String name) {
        String userAgentValue;
        String result = null;
        if ((userAgentValue = getHeaders("User-Agent")) != null) {
            Pattern p = Pattern.compile("([^/\\s]+/[^/\\s]+)\\s?(\\([^\\(\\)]+\\))?((\\s[^/\\s]+/[^/\\s]+)+)?");
            Matcher m = p.matcher(userAgentValue);
            if (m.find()) {
                if (name.equals("compatible")) {
                    result = m.group(1);
                }
                else if (name.equals("platform")) {
                    result = m.group(2);
                }
                else if (name.equals("browser")) {
                    result = m.group(3);
                }
            }
        }
        return result;
    }

    /**
     * The "Upgrade" header field is intended to provide a simple mechanism
     * for transitioning from HTTP/1.1 to some other protocol on the same
     * connection.
     * Each protocol name and version pair splits by ","
     * Example - Upgrade: HTTP/2.0,SHTTP/1.3,IRC/6.9,RTA/x11
     *
     * @return String[]
     */
    public String[] getUpgrade() {
        return getHeaders("Upgrade").split(",\\s?");
    }

    /**
     * The If-Modified-Since request HTTP header makes the request conditional:
     * the server will send back the requested resource, with a 200 status
     * , only if it has been last modified after the given date.
     *
     * @return String
     */
    public String getIfModifiedSince() {
        return getHeaders("If-Modified-Since");
    }
}
