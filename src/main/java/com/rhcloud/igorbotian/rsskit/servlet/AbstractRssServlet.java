package com.rhcloud.igorbotian.rsskit.servlet;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.rometools.rome.io.XmlReader;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public abstract class AbstractRssServlet extends RssKitServlet {

    private static final String RSS_CONTENT_TYPE = "application/rss+xml";

    protected SyndFeed downloadRssFeed(URL url) throws IOException {
        assert url != null;

        try {
            SyndFeedInput rssInput = new SyndFeedInput();
            return rssInput.build(new XmlReader(url));
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
