package com.rhcloud.igorbotian.rsskit.rss.championat;

import com.rhcloud.igorbotian.rsskit.rss.RssModifier;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class BreakingNewsFilter implements RssModifier {

    @Override
    public void apply(SyndFeed feed) {
        Objects.requireNonNull(feed);

        List<SyndEntry> breakingNews = getBreakingNews(feed);
        feed.setEntries(breakingNews);
    }

    private List<SyndEntry> getBreakingNews(SyndFeed feed) {
        assert feed != null;

        List<SyndEntry> breakingNews = new ArrayList<>();

        for (SyndEntry entry : feed.getEntries()) {
            if (isBreakingNews(entry)) {
                breakingNews.add(entry);
            }
        }

        return breakingNews;
    }

    private boolean isBreakingNews(SyndEntry entry) {
        assert entry != null;

        for (SyndCategory category : entry.getCategories()) {
            if ("breaking".equals(category.getName())) {
                return true;
            }
        }

        return false;
    }
}
