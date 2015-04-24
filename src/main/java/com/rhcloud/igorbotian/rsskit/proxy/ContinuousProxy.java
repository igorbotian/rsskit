package com.rhcloud.igorbotian.rsskit.proxy;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class ContinuousProxy extends HttpProxy {

    private static final String TEXT_HTML = "text/html";
    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private final HttpLinkMapper linkMapper;

    public ContinuousProxy(HttpLinkMapper mapper) {
        this.linkMapper = Objects.requireNonNull(mapper);
    }

    @Override
    protected void transfer(HttpURLConnection src, HttpServletResponse dest) throws IOException {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dest);

        URL url = new URL(URLDecoder.decode(src.getURL().toExternalForm(), UTF8.name()));
        String contentType = src.getContentType();
        Charset charset = UTF8;

        if (contentType == null) {
            contentType = TEXT_HTML;
        } else {
            charset = extractCharset(contentType);
        }

        if (contentType.contains(TEXT_HTML)) {
            String html = IOUtils.toString(src.getInputStream(), charset);
            html = mapLinks(url, html, charset);

            transferHeaders(html, src, dest);
            transferContents(html, dest);
        } else {
            ProxyFactory.raw().transfer(src.getURL(), dest);
        }
    }

    private Charset extractCharset(String contentType) throws UnsupportedEncodingException {
        String key = "charset=";

        if (contentType.contains(key)) {
            try {
                return Charset.forName(contentType.substring(contentType.indexOf(key) + key.length()));
            } catch (UnsupportedCharsetException e) {
                throw new UnsupportedEncodingException(e.getMessage());
            }
        }

        return UTF8;
    }

    private String mapLinks(URL address, String html, Charset charset) throws UnsupportedEncodingException {
        assert address != null;
        assert html != null;
        assert charset != null;

        Document htmlDoc = Jsoup.parse(html, charset.name());
        htmlDoc.outputSettings().escapeMode(Entities.EscapeMode.base);
        htmlDoc.outputSettings().charset(UTF8);

        mapLinks(address, htmlDoc, charset, "a", "href");
        mapLinks(address, htmlDoc, charset, "link", "href");
        mapLinks(address, htmlDoc, charset, "script", "src");
        mapLinks(address, htmlDoc, charset, "img", "src");

        return StringEscapeUtils.unescapeHtml4(htmlDoc.outerHtml());
    }

    private void mapLinks(URL address, Document htmlDoc, Charset charset, String tagName, String attrName)
            throws UnsupportedEncodingException {

        assert address != null;
        assert htmlDoc != null;
        assert charset != null;
        assert tagName != null;
        assert attrName != null;

        Elements tags = htmlDoc.select(tagName + "[" + attrName + "]");

        for (Element tag : tags) {
            mapLink(address, tag, attrName, charset);
        }
    }

    private void mapLink(URL address, Element tag, String attrName, Charset charset) throws UnsupportedEncodingException {
        assert tag != null;
        assert attrName != null;
        assert charset != null;

        try {
            String url = URLDecoder.decode(tag.attr(attrName), charset.name());

            if (!url.startsWith("http") && !url.startsWith("https")) {
                if (url.startsWith("//")) {
                    url = address.getProtocol() + ":" + url;
                } else {
                    String prefix = address.getProtocol() + "://" + address.getHost();

                    if (address.getPort() != -1) {
                        prefix += ":" + address.getPort();
                    }

                    if (StringUtils.isNotEmpty(address.getPath())) {
                        prefix += "/" + address.getPath();
                    }

                    if (url.startsWith("/")) {
                        url = prefix + url;
                    } else {
                        url = prefix + "/" + url;
                    }
                }
            }

            tag.attr(attrName, linkMapper.map(new URL(url)).toString());
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace(); // skip this link
        }
    }

    private void transferHeaders(String html, HttpURLConnection src, HttpServletResponse dest)
            throws IOException {

        assert html != null;
        assert src != null;
        assert dest != null;

        for (String header : src.getHeaderFields().keySet()) {
            if (header != null && !header.toLowerCase().contains("encoding")) {
                dest.addHeader(header, src.getHeaderField(header));
            }
        }

        dest.setContentType(TEXT_HTML + "; charset=" + UTF8.name().toLowerCase());
        dest.setContentLength(html.getBytes(UTF8).length);
        dest.setStatus(src.getResponseCode());
        dest.setCharacterEncoding(UTF8.name().toLowerCase());
    }

    private void transferContents(String html, HttpServletResponse dest) throws IOException {
        assert html != null;
        assert dest != null;

        try (InputStream is = new ByteArrayInputStream(html.getBytes(UTF8))) {
            try (OutputStream os = dest.getOutputStream()) {
                IOUtils.copy(is, os);
            }
        }
    }
}
