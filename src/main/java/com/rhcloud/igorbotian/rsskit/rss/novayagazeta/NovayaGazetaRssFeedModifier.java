package com.rhcloud.igorbotian.rsskit.rss.novayagazeta;

import com.rhcloud.igorbotian.rsskit.rss.LinkMapper;
import com.rhcloud.igorbotian.rsskit.rss.RssLinkMapper;
import com.rhcloud.igorbotian.rsskit.rss.RssModifier;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class NovayaGazetaRssFeedModifier implements RssModifier {

    private static final RssLinkMapper linkMapper = new RssLinkMapper(new ToPDAPrintVersionLinkMapper());

    @Override
    public void apply(SyndFeed feed) {
        Objects.requireNonNull(feed);

        List<SyndEntry> investigations = getInvestigations(feed);
        feed.setEntries(investigations);

        linkMapper.apply(feed);
    }

    private List<SyndEntry> getInvestigations(SyndFeed feed) {
        assert feed != null;

        List<SyndEntry> investigations = new ArrayList<>();

        for (SyndEntry entry : feed.getEntries()) {
            if (isInvestigation(entry)) {
                investigations.add(entry);
            }
        }

        return investigations;
    }

    private boolean isInvestigation(SyndEntry entry) {
        assert entry != null;

        for (SyndCategory category : entry.getCategories()) {
            if ("Расследования".equals(category.getName())) {
                return true;
            }
        }

        return false;
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
