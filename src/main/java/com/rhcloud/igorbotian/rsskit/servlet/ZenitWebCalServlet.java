package com.rhcloud.igorbotian.rsskit.servlet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class ZenitWebCalServlet extends RssKitServlet {

    private static final String ZENIT_CALENDAR_URL = "http://fc-zenit.ru/res/ical/ical.php?actionclass=ical&action=getcal";

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        WebCal calendar = downloadOriginalCalendarContents();
        respond(
                getPolishedContentOf(calendar).getBytes(calendar.encoding),
                "text/calendar",
                calendar.encoding,
                response
        );
    }

    private String getPolishedContentOf(WebCal calendar) throws IOException {
        StringBuilder result = new StringBuilder();

        for(String line : calendar.content) {
            result.append(
                    polishCalendarLine(line)
            );
            result.append("\n"); // such the ending is used in webcal
        }

        return result.toString();
    }

    private String polishCalendarLine(String line) {
        if(line.toLowerCase().startsWith("summary:")) {
            String[] chunks = StringUtils.split(
                    line.substring("summary:".length()),
                    "\\,"
            );
            trimAll(chunks);
            ArrayUtils.reverse(chunks);
            return "SUMMARY:" + StringUtils.join(chunks, "\\, ");
        }

        return line;
    }

    private static void trimAll(String[] values) {
        for(int i = 0; i < values.length; i++) {
            values[i] = values[i].trim();
        }
    }

    private WebCal downloadOriginalCalendarContents() throws IOException {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            try (CloseableHttpResponse response = httpClient.execute(new HttpGet(ZENIT_CALENDAR_URL))) {
                return new WebCal(
                        headersMapOf(response),
                        IOUtils.readLines(response.getEntity().getContent()),
                        ContentType.getOrDefault(response.getEntity()).getCharset()
                );
            }
        }
    }

    private Map<String, String> headersMapOf(CloseableHttpResponse response) {
        Map<String, String> result = new HashMap<>();

        for(Header header : response.getAllHeaders()) {
            result.put(header.getName(), header.getValue());
        }

        return result;
    }

    private static class WebCal {

        final Map<String, String> httpHeaders;
        final List<String> content;
        final Charset encoding;

        WebCal(Map<String, String> httpHeaders, List<String> content, Charset encoding) {
            this.httpHeaders = Collections.unmodifiableMap(httpHeaders);
            this.content = Collections.unmodifiableList(content);
            this.encoding = encoding != null ? encoding : Charset.forName("UTF-8");
        }
    }
}
