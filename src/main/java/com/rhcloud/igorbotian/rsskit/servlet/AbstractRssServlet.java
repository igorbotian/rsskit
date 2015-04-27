package com.rhcloud.igorbotian.rsskit.servlet;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public abstract class AbstractRssServlet extends RssKitServlet {

    protected static final String RSS_CONTENT_TYPE = "application/rss+xml";
    protected static final Charset UTF_8 = StandardCharsets.UTF_8;

    protected void transfer(SyndFeed rss, HttpServletResponse resp) throws IOException {
        assert rss != null;
        assert resp != null;

        try {
            SyndFeedOutput output = new SyndFeedOutput();
            byte[] contents = output.outputString(rss).getBytes(UTF_8);
            transferHeaders(contents, resp);
            transferContent(contents, resp);
        } catch (FeedException e) {
            throw new IOException("Failed to download original RSS feed", e);
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
