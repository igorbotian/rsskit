package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rss.RssDescriptionExtender;
import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizers;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class RssDescriptionExtendingServlet extends AbstractRssServlet {

    private static final String URL_PARAM = "url";
    private static final int MAX_ENTRIES = 10;
    private static final RssDescriptionExtender descriptionExtender = new RssDescriptionExtender(
            Mobilizers.instapaper()
    );

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        String url = request.getParameter(URL_PARAM);

        if (StringUtils.isNotEmpty(url)) {
            processRequest(new URL(url), request, response);
        }
    }

    protected void processRequest(URL url, HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        assert url != null;
        assert request != null;
        assert response != null;

        SyndFeed feed = downloadRssFeed(url);
        truncateToMaxEntriesAllowed(feed);
        respond(descriptionExtender.apply(feed), response);
    }

    private void truncateToMaxEntriesAllowed(SyndFeed feed) {
        assert feed != null;

        List<SyndEntry> entries = feed.getEntries();
        int maxEntries = Math.max(MAX_ENTRIES, entries.size());
        entries = entries.subList(0, maxEntries);

        feed.setEntries(entries);
    }
}
