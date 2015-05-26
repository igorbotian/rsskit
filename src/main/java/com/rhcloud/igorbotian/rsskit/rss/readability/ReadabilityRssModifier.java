package com.rhcloud.igorbotian.rsskit.rss.readability;

import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizers;
import com.rhcloud.igorbotian.rsskit.rss.*;
import com.rhcloud.igorbotian.rsskit.utils.RSSUtils;
import com.rometools.rome.feed.synd.SyndFeed;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class ReadabilityRssModifier implements RssModifier {

    private static final LinkMapper linkMapper = new ReadabilityLinkMapper();
    private static final int DEFAULT_MAX_ENTRIES = 10;

    @Override
    public void apply(SyndFeed feed) {
        Objects.requireNonNull(feed);

        RSSUtils.truncate(feed, DEFAULT_MAX_ENTRIES);
        RSSUtils.mapLinks(feed, linkMapper);
        RSSUtils.extendDescription(feed, Mobilizers.readability());
    }

    private static class ReadabilityLinkMapper implements LinkMapper {

        @Override
        public URL map(URL link) throws URISyntaxException, MalformedURLException {
            Objects.requireNonNull(link);

            URIBuilder builder = new URIBuilder(link.toString());

            for (NameValuePair pair : builder.getQueryParams()) {
                if ("read".equals(pair.getName())) {
                    return new URL(pair.getValue());
                }
            }

            return link;
        }
    }
}
