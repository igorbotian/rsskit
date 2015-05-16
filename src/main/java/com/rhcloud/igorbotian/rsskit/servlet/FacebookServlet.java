package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rss.facebook.FacebookRssDescriptionExtender;
import com.rometools.rome.feed.synd.SyndFeed;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class FacebookServlet extends AbstractRssServlet {

    private static final String RSS_PARAM = "rss";
    private static final FacebookRssDescriptionExtender descriptionExtender = new FacebookRssDescriptionExtender();

    @Override
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Objects.requireNonNull(req);
        Objects.requireNonNull(resp);

        String rssURL = req.getParameter(RSS_PARAM);

        if(StringUtils.isNotEmpty(rssURL)) {
            respondRss(rssURL, resp);
        }
    }

    private void respondRss(String rssURL, HttpServletResponse resp) throws IOException {
        assert rssURL != null;
        assert resp != null;

        SyndFeed feed = downloadRssFeed(new URL(rssURL));
        descriptionExtender.apply(feed);
        respond(feed, resp);
    }
}
