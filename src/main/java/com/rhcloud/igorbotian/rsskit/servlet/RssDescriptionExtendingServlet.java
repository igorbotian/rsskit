package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizers;
import com.rhcloud.igorbotian.rsskit.rss.RssDescriptionExtender;
import com.rhcloud.igorbotian.rsskit.rss.RssTruncater;
import com.rometools.rome.feed.synd.SyndFeed;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class RssDescriptionExtendingServlet extends AbstractRssServlet {

    private static final String URL_PARAM = "url";
    private static final String SERVICE_PARAM = "service";
    private static final int MAX_ENTRIES = 10;

    private static final RssDescriptionExtender instapaperDescriptionExtender = new RssDescriptionExtender(
            Mobilizers.instapaper()
    );

    private static final RssDescriptionExtender readabilityDescriptionExtender = new RssDescriptionExtender(
            Mobilizers.readability()
    );

    private static final RssTruncater truncater = new RssTruncater(MAX_ENTRIES);

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        String url = request.getParameter(URL_PARAM);

        if (StringUtils.isNotEmpty(url)) {
            RssDescriptionExtender descriptionExtender =
                    "readability".equals(request.getParameter(SERVICE_PARAM))
                            ? readabilityDescriptionExtender
                            : instapaperDescriptionExtender;
            processRequest(new URL(url), descriptionExtender, request, response);
        }
    }

    protected void processRequest(URL url, RssDescriptionExtender descriptionExtender,
                                  HttpServletRequest request, HttpServletResponse response) throws IOException {

        assert url != null;
        assert request != null;
        assert response != null;

        SyndFeed feed = downloadRssFeed(url);
        feed = truncater.apply(feed);
        respond(descriptionExtender.apply(feed), response);
    }
}
