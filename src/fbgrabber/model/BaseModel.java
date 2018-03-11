/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fbgrabber.model;

import fbgrabber.common.CommonUtils;
import fbgrabber.common.DebugLog;
import java.util.Map;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author xuguobiao
 */
public class BaseModel {

    protected final static int TIMEOUT_CONNECTION = 60000;

    protected final static String HTTP_GET = "GET";
    protected final static String HTTP_POST = "POST";

//    protected String userAgent = "Mozilla/5.0 (Linux; U; Android 7.0.0; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
    protected static String userAgent = " Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11";
    protected static Map<String, String> cookies = null;

    public void setUserAgent(String ua) {
        userAgent = ua;
    }

    private Connection getConnection(String url) {
        return Jsoup.connect(url)
                .timeout(TIMEOUT_CONNECTION)
                .userAgent(userAgent)
                .followRedirects(true)
                .ignoreContentType(true);
    }

    protected Document requestDocument(String url, String httpMethod, Map<String, String> data) throws Exception {
        DebugLog.log("request url->" + url);
        DebugLog.log("httpMethod->" + httpMethod);
        Connection connection = getConnection(url);
        if (data != null && data.size() > 0) {
            connection.data(data);
            DebugLog.log("request data->" + CommonUtils.map2String(data));
        }
        if (cookies != null) {
            DebugLog.log("set Cookies->" + cookies);
            connection.cookies(cookies);
        }
        Document resultDocument = HTTP_POST.equalsIgnoreCase(httpMethod) ? connection.post() : connection.get();

        return resultDocument;
    }

    protected Response requestBody(String url, String httpMethod, Map<String, String> data) throws Exception {
        DebugLog.log("request url->" + url);
        DebugLog.log("httpMethod->" + httpMethod);
        Connection connection = getConnection(url);
        if (data != null && data.size() > 0) {
            connection.data(data);
            DebugLog.log("request data->" + CommonUtils.map2String(data));
        }
        if (cookies != null) {
            connection.cookies(cookies);
        }
        connection.method(HTTP_POST.equalsIgnoreCase(httpMethod) ? Connection.Method.POST : Connection.Method.GET);
        Connection.Response res = connection.execute();

        DebugLog.log("resultCookies->" + res.cookies());
        if (res.cookies() != null && !res.cookies().isEmpty()) {
            cookies = res.cookies();
        }
        return res;
    }

    protected String requestBodyString(String url, String httpMethod, Map<String, String> data) throws Exception {
        Connection.Response res = requestBody(url, httpMethod, data);

        String resultBody = res.body();
        DebugLog.log("resultBody->" + resultBody);

        return resultBody;
    }

    protected byte[] requestImage(String url, String httpMethod, Map<String, String> data) throws Exception {
        DebugLog.log("request url->" + url);
        DebugLog.log("httpMethod->" + httpMethod);
        Connection connection = getConnection(url);
        if (data != null && data.size() > 0) {
            connection.data(data);
            DebugLog.log("request data->" + CommonUtils.map2String(data));
        }
        if (cookies != null) {
            connection.cookies(cookies);
        }
        connection.method(HTTP_POST.equalsIgnoreCase(httpMethod) ? Connection.Method.POST : Connection.Method.GET);
        connection.ignoreContentType(true);
        Connection.Response res = connection.execute();

        byte[] resultBody = res.bodyAsBytes();

        DebugLog.log("resultCookies->" + res.cookies());
        if (res.cookies() != null && !res.cookies().isEmpty()) {
            cookies = res.cookies();
        }
        return resultBody;
    }
}
