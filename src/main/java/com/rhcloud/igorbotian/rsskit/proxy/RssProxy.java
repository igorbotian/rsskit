package com.rhcloud.igorbotian.rsskit.proxy;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.rometools.rome.io.XmlReader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class RssProxy extends HttpProxy {

    private static final String RSS_CONTENT_TYPE = "application/rss+xml";
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    private final int maxEntries;

    public RssProxy(int maxEntries) {
        if (maxEntries < 1) {
            throw new IllegalArgumentException("Maximum number of entries should have a positive value");
        }

        this.maxEntries = maxEntries;
    }

    @Override
    protected void transfer(HttpURLConnection src, HttpServletResponse dest) throws IOException {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dest);

        try {
            processRequest(src, dest);
        } catch (FeedException e) {
            throw new IOException("Failed to process request", e);
        }
    }

    private void processRequest(HttpURLConnection src, HttpServletResponse dest) throws IOException, FeedException {
        assert src != null;
        assert dest != null;

        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(src.getInputStream()));
        updateToFullItemDescriptions(feed);

        SyndFeedOutput output = new SyndFeedOutput();
        byte[] rss = output.outputString(feed).getBytes(UTF_8);

        transferHeaders(src, rss, dest);
        transferContent(rss, dest);
    }

    private void updateToFullItemDescriptions(SyndFeed feed) throws FeedException {
        assert feed != null;

        int count = Math.min(maxEntries, feed.getEntries().size());
        List<SyndEntry> truncatedEntries = new ArrayList<>(count);
        truncatedEntries.addAll(feed.getEntries().subList(0, count));

        feed.setEntries(truncatedEntries);

        for (SyndEntry item : truncatedEntries) {
            try {
                updateToFullDescription(item);
            } catch (IOException e) {
                e.printStackTrace(); // skip this item
            }
        }
    }

    private void updateToFullDescription(SyndEntry item) throws IOException {
        assert item != null;

        SyndContent fullDescription = new SyndContentImpl();
        fullDescription.setType("text/html");
        fullDescription.setValue(downloadContents(new URL(item.getLink())));

        item.setDescription(fullDescription);
    }

    private String downloadContents(URL link) throws IOException {
        assert link != null;

        link = updateIfReadabilityLink(link);

        Connection conn = Jsoup.connect(Mobilizers.INSTAPAPER_SERVICE_URL + link.toString());
        Document htmlDoc = conn.get();
        htmlDoc.outputSettings().charset(UTF_8);
        htmlDoc.outputSettings().escapeMode(Entities.EscapeMode.base);

        return StringEscapeUtils.unescapeHtml4(htmlDoc.getElementsByTag("main").html());
    }

    private URL updateIfReadabilityLink(URL url) throws MalformedURLException {
        assert url != null;

        URL link = url;

        if (!link.getHost().contains("readability.com")) {
            return url;
        }

        try {
            URIBuilder builder = new URIBuilder(link.toString());
            for (NameValuePair param : builder.getQueryParams()) {
                if ("url".equals(param.getName())) {
                    url = new URL(param.getValue());
                    break;
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace(); // skip this link
        }

        return url;
    }

    private void transferHeaders(HttpURLConnection src, byte[] content, HttpServletResponse dest) {
        assert src != null;
        assert content != null;
        assert dest != null;

        for (String header : src.getHeaderFields().keySet()) {
            if (StringUtils.isNotEmpty(header) && !header.toLowerCase().contains("encoding")) {
                dest.setHeader(header, src.getHeaderField(header));
            }
        }

        dest.setContentType(RSS_CONTENT_TYPE);
        dest.setContentLength(content.length);
        dest.setCharacterEncoding(UTF_8.name().toLowerCase());
    }

    private void transferContent(byte[] content, HttpServletResponse dest) throws IOException {
        assert content != null;
        assert dest != null;

        try (InputStream is = new ByteArrayInputStream(content)) {
            try (OutputStream os = dest.getOutputStream()) {
                IOUtils.copy(is, os);
            }
        }
    }
}
