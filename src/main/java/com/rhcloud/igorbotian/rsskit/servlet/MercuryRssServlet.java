package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rss.mercury.MercuryRssModifier;
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
public class MercuryRssServlet extends AbstractRssServlet {

    private static final String URL_PARAM = "url";
    private static final MercuryRssModifier rssModifier = new MercuryRssModifier();

    @Override
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Objects.requireNonNull(req);
        Objects.requireNonNull(resp);

        String url = req.getParameter(URL_PARAM);

        if(StringUtils.isNotEmpty(url)) {
            SyndFeed feed = downloadRssFeed(new URL(url));
            rssModifier.apply(feed);
            respond(feed, resp);
        }
    }
}
