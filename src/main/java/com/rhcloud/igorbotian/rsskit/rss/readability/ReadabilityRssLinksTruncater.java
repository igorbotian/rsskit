package com.rhcloud.igorbotian.rsskit.rss.readability;

import com.rhcloud.igorbotian.rsskit.rss.LinkMapper;
import com.rhcloud.igorbotian.rsskit.rss.RssLinkMapper;
import com.rhcloud.igorbotian.rsskit.rss.RssModifier;
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
public class ReadabilityRssLinksTruncater implements RssModifier, LinkMapper {

    private final RssLinkMapper mapper = new RssLinkMapper(this);

    @Override
    public SyndFeed apply(SyndFeed original) {
        return mapper.apply(original);
    }


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
