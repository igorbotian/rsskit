package com.rhcloud.igorbotian.rsskit.rss;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class RssTruncater implements RssModifier {

    private final int maxEntries;

    public RssTruncater(int maxEntries) {
        if(maxEntries < 1) {
            throw new IllegalArgumentException("Maximum entries allowed should have a positive value");
        }

        this.maxEntries = maxEntries;
    }

    @Override
    public void apply(SyndFeed feed) {
        Objects.requireNonNull(feed);
        truncate(feed);
    }

    private void truncate(SyndFeed feed) {
        assert feed != null;

        List<SyndEntry> entries = feed.getEntries();
        int count = Math.min(maxEntries, entries.size());
        feed.setEntries(entries.subList(0, count));
    }
}
