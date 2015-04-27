package com.rhcloud.igorbotian.rsskit.proxy;

import com.rhcloud.igorbotian.rsskit.filter.RssFilter;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.rometools.rome.io.XmlReader;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public abstract class RssFilteringProxy implements Proxy {

    protected static String RSS_MIME_TYPE = "application/rss+xml";

    private final RssFilter rssFilter;

    public RssFilteringProxy(RssFilter filter) {
        this.rssFilter = Objects.requireNonNull(filter);
    }

    @Override
    public final void transfer(URL src, HttpServletResponse dest) throws IOException {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dest);

        SyndFeed originalRss = downloadRssFeed(src);
        SyndFeed filteredRss = rssFilter.apply(originalRss);
        transferRss(filteredRss, dest);
    }

    private SyndFeed downloadRssFeed(URL url) throws IOException {
        assert url != null;

        try {
            SyndFeedInput rssInput = new SyndFeedInput();
            return rssInput.build(new XmlReader(url));
        } catch (FeedException e) {
            throw new IOException("Failed to retrieve an RSS feed: " + url.toString(), e);
        }
    }

    private void transferRss(SyndFeed rss, HttpServletResponse resp) throws IOException {
        assert rss != null;
        assert rss != resp;

        try {
            SyndFeedOutput rssOutput = new SyndFeedOutput();
            byte[] rssBytes = rssOutput.outputString(rss).getBytes(StandardCharsets.UTF_8);
            transferHeaders(rssBytes, resp);
            transferContent(rssBytes, resp);
        } catch (FeedException e) {
            throw new IOException("Failed to transfer a filtered RSS feed: " + rss.getLink(), e);
        }
    }

    private void transferHeaders(byte[] content, HttpServletResponse resp) {
        assert content != null;
        assert resp != null;

        resp.setContentType(RSS_MIME_TYPE);
        resp.setContentLength(content.length);
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name().toLowerCase());
    }

    private void transferContent(byte[] content, HttpServletResponse resp) throws IOException {
        assert content != null;
        assert resp != null;

        try (InputStream is = new ByteArrayInputStream(content)) {
            try (OutputStream os = resp.getOutputStream()) {
                IOUtils.copy(is, os);
            }
        }
    }
}
