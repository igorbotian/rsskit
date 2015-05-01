package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rss.RssModifier;
import com.rometools.rome.feed.synd.SyndFeed;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class RssFilteringServlet extends AbstractRssServlet {

    private final RssModifier rssModifier;
    private final URL rssURL;

    public RssFilteringServlet(URL rssURL, RssModifier rssModifier) {
        this.rssURL = Objects.requireNonNull(rssURL);
        this.rssModifier = rssModifier;
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        assert request != null;
        assert response != null;

        SyndFeed feed = downloadRssFeed(rssURL);
        rssModifier.apply(feed);
        respond(feed, response);
    }
}
