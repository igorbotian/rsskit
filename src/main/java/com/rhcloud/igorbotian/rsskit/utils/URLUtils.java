package com.rhcloud.igorbotian.rsskit.utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public final class URLUtils {

    private URLUtils() {
        //
    }

    public static URL makeServletURL(HttpServletRequest request) throws MalformedURLException {
        Objects.requireNonNull(request);

        Integer port = request.getServerPort();

        if (port == 80) {
            port = null; // just to not to show in the URL
        }

        return makeURL(
                request.getScheme(),
                request.getServerName(),
                port,
                request.getServletPath(),
                Collections.<String, String>emptyMap()
        );
    }

    public static URL makeURL(String url, List<NameValuePair> params) throws MalformedURLException {
        Objects.requireNonNull(url);
        Objects.requireNonNull(params);

        Map<String, String> paramMap = new HashMap<>();

        for (NameValuePair param : params) {
            paramMap.put(param.getName(), param.getValue());
        }

        return makeURL(url, paramMap);
    }

    public static URL makeURL(String url, Map<String, String> params) throws MalformedURLException {
        Objects.requireNonNull(url);
        Objects.requireNonNull(params);

        try {
            URIBuilder builder = new URIBuilder(url);

            for (String param : params.keySet()) {
                builder.setParameter(param, params.get(param));
            }

            return builder.build().toURL();
        } catch (URISyntaxException e) {
            throw new MalformedURLException(e.getMessage());
        }
    }

    public static URL makeURL(String protocol, String host, Integer port, String path, Map<String, String> params)
            throws MalformedURLException {

        URIBuilder builder = new URIBuilder();
        builder.setScheme(protocol);
        builder.setHost(host);

        if (port != null && port > 0) {
            builder.setPort(port);
        }

        builder.setPath(path);

        try {
            return makeURL(builder.build().toString(), params);
        } catch (URISyntaxException e) {
            throw new MalformedURLException(e.getMessage());
        }
    }

    public static String getDocumentTitle(URL url) throws IOException {
        Objects.requireNonNull(url);

        Document html = Jsoup.connect(url.toString()).get();
        Elements titles = html.select("title");

        return (titles.size() > 0) ? titles.get(0).text() : "";
    }
}
