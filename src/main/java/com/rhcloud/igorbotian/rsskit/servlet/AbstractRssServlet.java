package com.rhcloud.igorbotian.rsskit.servlet;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

/**
 * @author Igor Botian
 */
public abstract class AbstractRssServlet extends RssKitServlet {

    private static final String RSS_CONTENT_TYPE = "application/rss+xml";

    protected SyndFeed downloadRssFeed(URL url) throws IOException {
        assert url != null;

        try {
            URLConnection openConnection = url.openConnection();
            InputStream is = null;

            try {
                is = openConnection.getInputStream();

                if ("gzip".equals(openConnection.getContentEncoding())) {
                    is = new GZIPInputStream(is);
                }

                return new SyndFeedInput().build(new InputSource(is));
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } catch (FeedException e) {
            throw new IOException("Failed to retrieve an RSS feed: " + url.toString(), e);
        }
    }

    protected void respond(SyndFeed rss, HttpServletResponse resp) throws IOException {
        assert rss != null;
        assert resp != null;

        try {
            SyndFeedOutput output = new SyndFeedOutput();
            byte[] data = output.outputString(rss).getBytes(StandardCharsets.UTF_8);
            respond(data, RSS_CONTENT_TYPE, StandardCharsets.UTF_8, resp);
        } catch (FeedException e) {
            throw new IOException("Failed to download original RSS feed", e);
        }
    }
}
