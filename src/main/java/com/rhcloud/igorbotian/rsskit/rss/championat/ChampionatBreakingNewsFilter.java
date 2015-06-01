package com.rhcloud.igorbotian.rsskit.rss.championat;

import com.rhcloud.igorbotian.rsskit.rss.RssModifier;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class ChampionatBreakingNewsFilter implements RssModifier {

    @Override
    public void apply(SyndFeed feed) {
        Objects.requireNonNull(feed);

        List<SyndEntry> entries = new ArrayList<>(feed.getEntries().size());

        for(SyndEntry entry : feed.getEntries()) {
            if(isBreaking(entry)) {
                entries.add(entry);
            }
        }

        feed.setEntries(entries);
    }

    private boolean isBreaking(SyndEntry entry) {
        assert entry != null;

        for(SyndCategory category : entry.getCategories()) {
            if(ChampionatRssGenerator.BREAKING_CATEGORY.equals(category.getName())) {
                return true;
            }
        }

        return false;
    }
}
