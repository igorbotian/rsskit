package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizer;
import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizers;
import com.rhcloud.igorbotian.rsskit.rss.*;
import com.rhcloud.igorbotian.rsskit.utils.RSSUtils;
import com.rometools.rome.feed.synd.SyndFeed;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class RssDescriptionExtendingServlet extends AbstractRssServlet {

    private static final Logger LOGGER = LogManager.getLogger(RssDescriptionExtendingServlet.class);
    private static final String URL_PARAM = "url";
    private static final String SERVICE_PARAM = "service";
    private static final String CATEGORIES_PARAM = "categories";
    private static final String SIZE_PARAM = "size";
    private static final String MOBILE_VERSION_HOST_PARAM = "mobile_version_host";
    private static final int DEFAULT_MAX_ENTRIES = 10;

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        String url = request.getParameter(URL_PARAM);

        if (StringUtils.isNotEmpty(url)) {
            Mobilizer mobilizer = "mercury".equals(request.getParameter(SERVICE_PARAM))
                            ? Mobilizers.mercury() : Mobilizers.instapaper();
            Set<String> categories = parseCategories(request.getParameter(CATEGORIES_PARAM));
            Integer size = parseSize(request.getParameter(SIZE_PARAM));
            String mobileVersionHost = request.getParameter(MOBILE_VERSION_HOST_PARAM);
            SyndFeed feed = getExtendedRSS(new URL(url), mobilizer, categories, size, mobileVersionHost);
            respond(feed, response);
        }
    }

    private int parseSize(String param) {
        try {
            if (param != null) {
                return Integer.parseInt(param);
            }
        } catch (NumberFormatException e) {
            LOGGER.trace("SIZE integer parameter is not a number: " + param);
        }

        return DEFAULT_MAX_ENTRIES;
    }

    private Set<String> parseCategories(String param) {
        Set<String> categories = new HashSet<>();

        if (param != null) {
            String paramUTF8 = new String(param.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

            for (String category : StringUtils.split(paramUTF8, ",")) {
                String trimmed = category.trim();

                if (StringUtils.isNotEmpty(trimmed)) {
                    categories.add(trimmed);
                }
            }
        }

        return categories;
    }

    private SyndFeed getExtendedRSS(URL rssURL, Mobilizer mobilizer, Set<String> categories, int size,
                                    String mobileVersionHost)
            throws IOException {

        assert rssURL != null;
        assert mobilizer != null;
        assert categories != null;
        assert size > 0;

        String mobileVersionHostTrimmed = mobileVersionHost != null ? mobileVersionHost.trim() : null;
        SyndFeed feed = downloadRssFeed(rssURL);
        RSSUtils.filterByCategories(feed, categories);
        RSSUtils.truncate(feed, size);

        if (StringUtils.isNotEmpty(mobileVersionHostTrimmed)) {
            RSSUtils.mapLinks(feed, new HostChanger(mobileVersionHostTrimmed));
        }

        RSSUtils.extendDescription(feed, mobilizer);

        return feed;
    }

    private static class HostChanger implements LinkMapper {

        private final String mobileVersionHost;

        private HostChanger(String mobileVersionHost) {
            this.mobileVersionHost = Objects.requireNonNull(mobileVersionHost);
        }

        @Override
        public URL map(URL link) throws URISyntaxException, MalformedURLException {
            Objects.requireNonNull(link);

            URIBuilder builder = new URIBuilder(link.toURI());
            builder.setHost(mobileVersionHost);
            return builder.build().toURL();
        }
    }
}
