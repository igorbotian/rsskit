package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.utils.RSSUtils;
import com.rhcloud.igorbotian.rsskit.utils.RssEntryFilter;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian
 */
public class AsotServlet extends AbstractRssServlet {

    private static final String CUENATION_RSS_URL = "http://cuenation.com/feed.php";

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        SyndFeed rss = downloadRssFeed(new URL(CUENATION_RSS_URL));
        RSSUtils.filter(rss, new RssEntryFilter() {

            @Override
            public boolean apply(SyndEntry entry) {
                return entry.getTitle().toLowerCase().contains("a state of trance");
            }
        });

        respond(rss, response);
    }
}
