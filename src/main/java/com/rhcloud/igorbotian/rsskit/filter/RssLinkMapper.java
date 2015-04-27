package com.rhcloud.igorbotian.rsskit.filter;

import com.rhcloud.igorbotian.rsskit.proxy.HttpLinkMapper;
import com.rhcloud.igorbotian.rsskit.utils.RssFeedUtils;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class RssLinkMapper implements RssFilter {

    private final HttpLinkMapper mapper;

    public RssLinkMapper(HttpLinkMapper mapper) {
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public SyndFeed apply(SyndFeed original) {
        Objects.requireNonNull(original);

        try {
            SyndFeed feed = RssFeedUtils.clone(original);
            mapLinks(feed);
            return feed;
        } catch (IOException e) {
            return original;
        }
    }

    private void mapLinks(SyndFeed feed) {
        assert feed != null;

        for(SyndEntry entry : feed.getEntries()) {
            try {
                mapLink(entry);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace(); // skip this link
            }
        }
    }

    private void mapLink(SyndEntry entry) throws IOException, URISyntaxException {
        assert entry != null;

        URL link = mapper.map(new URL(entry.getLink()));
        entry.setLink(link.toString());
    }
}
