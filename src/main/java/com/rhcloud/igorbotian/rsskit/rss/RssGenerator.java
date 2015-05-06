package com.rhcloud.igorbotian.rsskit.rss;

import com.rometools.rome.feed.synd.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public abstract class RssGenerator<T> {

    private static final SimpleDateFormat UTC_FORMAT = new SimpleDateFormat("yyyy-MM-dd KK:mm:ss a Z");

    public SyndFeed error(Exception ex) {
        Objects.requireNonNull(ex);

        SyndFeed skeleton = skeleton();
        SyndFeed rss = new SyndFeedImpl();
        rss.setDescription(skeleton.getDescription());
        rss.setTitle(skeleton.getTitle());
        rss.setLink(skeleton.getLink());
        rss.setPublishedDate(skeleton.getPublishedDate());
        rss.setAuthor(skeleton.getAuthor());
        rss.setFeedType(skeleton.getFeedType());

        SyndEntry entry = new SyndEntryImpl();

        SyndContent description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue(ex.getMessage());

        entry.setTitle("Error (" + UTC_FORMAT.format(new Date()) + ")");
        entry.setDescription(description);
        entry.setPublishedDate(new Date());

        List<SyndEntry> entries = new ArrayList<>();
        entries.addAll(skeleton.getEntries());
        entries.add(entry);

        rss.setEntries(entries);

        return rss;
    }

    public abstract SyndFeed generate(T obj);

    protected abstract SyndFeed skeleton();
}
