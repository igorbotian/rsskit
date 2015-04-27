package com.rhcloud.igorbotian.rsskit.championat;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class ChampionatRssFeed {

    private static final String RSS_URL = "http://www.championat.com/xml/rss_football.xml";

    public SyndFeed getBreakingNews() throws IOException {
        SyndFeed feed = downloadOriginalFeed();
        feed.setEntries(filterBreakingNewsOnly(feed));
        return feed;
    }

    private SyndFeed downloadOriginalFeed() throws IOException {
        try {
            SyndFeedInput input = new SyndFeedInput();
            return input.build(new XmlReader(new URL(RSS_URL)));
        } catch (FeedException e) {
            throw new IOException("Failed to download original RSS feed", e);
        }
    }

    private List<SyndEntry> filterBreakingNewsOnly(SyndFeed feed) {
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
