package com.rhcloud.igorbotian.rsskit.rss.novayagazeta;

import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizers;
import com.rhcloud.igorbotian.rsskit.rss.LinkMapper;
import com.rhcloud.igorbotian.rsskit.rss.RssModifier;
import com.rhcloud.igorbotian.rsskit.utils.RSSUtils;
import com.rometools.rome.feed.synd.SyndFeed;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class NovayaGazetaRssFeedModifier implements RssModifier {

    private static final ToPDAPrintVersionLinkMapper linkMapper = new ToPDAPrintVersionLinkMapper();

    @Override
    public void apply(SyndFeed feed) {
        Objects.requireNonNull(feed);

        RSSUtils.filterByCategories(feed, Collections.singleton("Расследования"));
        RSSUtils.mapLinks(feed, linkMapper);
        RSSUtils.extendDescription(feed, Mobilizers.instapaper());
    }

    private static class ToPDAPrintVersionLinkMapper implements LinkMapper {

        @Override
        public URL map(URL link) throws URISyntaxException, MalformedURLException {
            Objects.requireNonNull(link);

            URIBuilder builder = new URIBuilder(link.toURI());

            builder.setHost(StringUtils.replace(builder.getHost(), "www", "pda"));
            builder.setParameter("print", "1");

            return builder.build().toURL();
        }
    }
}
