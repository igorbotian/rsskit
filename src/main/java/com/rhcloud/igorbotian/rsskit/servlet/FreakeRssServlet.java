package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.freake.Freake;
import com.rhcloud.igorbotian.rsskit.freake.FreakeRssFeed;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class FreakeRssServlet extends RssKitServlet {

    private static final String RSS_CONTENT_TYPE = "application/rss+xml";
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static FreakeRssFeed feed;

    @Override
    public void init() throws ServletException {
        synchronized (FreakeRssServlet.class) {
            if(feed == null) {
                try {
                    feed = new FreakeRssFeed(new Freake());
                } catch (IOException e) {
                    throw new ServletException("Failed to initialize RSS feed", e);
                }
            }
        }
    }

    @Override
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        assert req != null;
        assert resp != null;

        SyndFeed rssFeed = feed.get();

        try {
            SyndFeedOutput output = new SyndFeedOutput();
            byte[] contents = output.outputString(rssFeed).getBytes(UTF_8);
            transferHeaders(contents, resp);
            transferContent(contents, resp);
        } catch (FeedException e) {
            throw new IOException("Failed to compose RSS feed of the top releases of the day", e);
        }
    }

    private void transferHeaders(byte[] content, HttpServletResponse resp) {
        assert content != null;
        assert resp != null;

        resp.setContentType(RSS_CONTENT_TYPE);
        resp.setContentLength(content.length);
        resp.setCharacterEncoding(UTF_8.name().toLowerCase());
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
