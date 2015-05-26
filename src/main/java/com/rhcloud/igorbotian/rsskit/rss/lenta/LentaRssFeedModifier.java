package com.rhcloud.igorbotian.rsskit.rss.lenta;

import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizers;
import com.rhcloud.igorbotian.rsskit.rss.LinkMapper;
import com.rhcloud.igorbotian.rsskit.rss.RssModifier;
import com.rhcloud.igorbotian.rsskit.utils.RSSUtils;
import com.rometools.rome.feed.synd.SyndFeed;
import org.apache.http.client.utils.URIBuilder;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class LentaRssFeedModifier implements RssModifier {

    private static final LinkMapper mobileVersionLinkMapper = new MobileVersionLinkMapper();

    @Override
    public void apply(SyndFeed feed) {
        Objects.requireNonNull(feed);

        RSSUtils.filterByCategories(feed, new HashSet<>(Arrays.asList("Экономика", "Бывший СССР")));
        RSSUtils.mapLinks(feed, mobileVersionLinkMapper);
        RSSUtils.extendDescription(feed, Mobilizers.instapaper());
    }

    private static class MobileVersionLinkMapper implements LinkMapper {

        private static final String MOBILE_VERSION_DOMAIN = "m.lenta.ru";

        @Override
        public URL map(URL url) throws URISyntaxException, MalformedURLException {
            assert url != null;

            URIBuilder builder = new URIBuilder(url.toURI());
            builder.setHost(MOBILE_VERSION_DOMAIN);
            return builder.build().toURL();
        }
    }
}
