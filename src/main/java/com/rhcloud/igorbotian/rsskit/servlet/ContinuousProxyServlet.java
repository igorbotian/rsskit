package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rss.LinkMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class ContinuousProxyServlet extends ProxyServlet {

    private static final String HTML_CONTENT_TYPE = "text/html";
    private static final Map<String, String> MAPPED_LINKS;
    private static LinkMapper mapper;

    static {
        Map<String, String> mappedLinks = new HashMap<>();
        mappedLinks.put("a", "href");
        mappedLinks.put("link", "href");
        mappedLinks.put("img", "src");
        mappedLinks.put("script", "src");

        MAPPED_LINKS = Collections.unmodifiableMap(mappedLinks);
    }

    @Override
    protected void processRequest(URL url, HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Objects.requireNonNull(url);
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        initLinkMapper(request);

        URLConnection connection = url.openConnection();
        String contentType = connection.getContentType();

        if(contentType.toLowerCase().startsWith(HTML_CONTENT_TYPE)) {
            Document htmlWithMappedLinks = mapLinks(url, request, response);
            respondHTML(htmlWithMappedLinks, request, response);
        } else {
            byte[] data = IOUtils.toByteArray(url);
            String encoding = connection.getContentEncoding();

            respond(data, contentType, encoding != null ? Charset.forName(encoding) : null, response);
        }
    }

    private LinkMapper initLinkMapper(HttpServletRequest request) throws MalformedURLException {
        assert request != null;

        synchronized (ContinuousProxyServlet.class) {
            if (mapper == null) {
                String host = request.getServerName();
                int port = request.getServerPort();
                String path = getServletContext().getContextPath();
                String name = getServletName();
                mapper = new LinkMapperImpl(new URL("http", host, port, path + "/" + name));
            }
        }

        return mapper;
    }

    private void respondHTML(Document htmlDoc, HttpServletRequest request, HttpServletResponse response) throws IOException {
        assert htmlDoc != null;
        assert request != null;
        assert response != null;

        htmlDoc.outputSettings().escapeMode(Entities.EscapeMode.base);
        htmlDoc.outputSettings().charset(StandardCharsets.UTF_8);

        String html = StringEscapeUtils.unescapeHtml4(htmlDoc.outerHtml());
        byte[] data = html.getBytes(StandardCharsets.UTF_8);
        respond(data, HTML_CONTENT_TYPE, StandardCharsets.UTF_8, response);
    }

    private Document downloadHtml(URL url) throws IOException {
        assert url != null;
        return Jsoup.connect(url.toString()).get();
    }

    private Document mapLinks(URL url, HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        assert url != null;
        assert request != null;
        assert response != null;

        Document htmlDoc = downloadHtml(url);

        for (Map.Entry<String, String> link : MAPPED_LINKS.entrySet()) {
            mapLinks(url, htmlDoc, link.getKey(), link.getValue());
        }

        return htmlDoc;
    }

    private void mapLinks(URL url, Document htmlDoc, String tagName, String attrName)
            throws UnsupportedEncodingException {

        assert htmlDoc != null;
        assert tagName != null;
        assert attrName != null;

        Elements tags = htmlDoc.select(tagName + "[" + attrName + "]");

        for (Element tag : tags) {
            mapLink(url, tag, attrName);
        }
    }

    private void mapLink(URL address, Element tag, String attrName) throws UnsupportedEncodingException {
        assert address != null;
        assert tag != null;
        assert attrName != null;

        try {
            String url = tag.attr(attrName);

            if (!url.startsWith("http") && !url.startsWith("https")) {
                String prefix = address.getProtocol() + "://";

                if (url.startsWith("//")) {
                    url = prefix + url.substring(2); // without '//'
                } else {
                    prefix += address.getHost();

                    if (address.getPort() != -1) {
                        prefix += ":" + address.getPort();
                    }

                    if (!url.startsWith("/")) {
                        if (StringUtils.isNotEmpty(address.getPath())) {
                            prefix += "/" + address.getPath();
                        }

                        prefix += "/";
                    }

                    url = prefix + url;
                }
            }

            tag.attr(attrName, mapper.map(new URL(url)).toString());
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace(); // skip this link
        }
    }

    private static class LinkMapperImpl implements LinkMapper {

        private final URL servletURL;

        public LinkMapperImpl(URL servletURL) {
            this.servletURL = Objects.requireNonNull(servletURL);
        }

        @Override
        public URL map(URL url) throws URISyntaxException, MalformedURLException {
            Objects.requireNonNull(url);

            URIBuilder builder = new URIBuilder();
            builder.setScheme(servletURL.getProtocol());
            builder.setHost(servletURL.getHost());
            builder.setPort(servletURL.getPort());
            builder.setPath(servletURL.getPath());
            builder.addParameter(URL_PARAM, url.toString());

            return builder.build().toURL();
        }
    }
}
