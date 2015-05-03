package com.rhcloud.igorbotian.rsskit.rss.buffer;

import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizers;
import com.rhcloud.igorbotian.rsskit.rest.buffer.BufferException;
import com.rhcloud.igorbotian.rsskit.rss.RssDescriptionExtender;
import com.rometools.rome.feed.synd.*;
import org.apache.http.NameValuePair;

import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class BufferRssGenerator {

    private static final RssDescriptionExtender descriptionExtender = new RssDescriptionExtender(Mobilizers.instapaper());

    public SyndFeed generate(List<NameValuePair> pendingUpdates) throws BufferException {
        Objects.requireNonNull(pendingUpdates);

        SyndFeed feed = generateFeed(pendingUpdates);
        descriptionExtender.apply(feed);
        return feed;
    }

    private SyndFeed generateFeed(List<NameValuePair> pendingUpdates) throws BufferException {
        assert pendingUpdates != null;

        SyndFeed feed = new SyndFeedImpl();
        feed.setTitle("Buffer pending updates");
        feed.setFeedType("rss_2.0");
        feed.setDescription("Provides pending updates from different Buffer profiles (Twitter, Facebook, etc.)");
        feed.setLink("http://www.buffer.com");

        List<SyndEntry> entries = new ArrayList<>(pendingUpdates.size());

        for (NameValuePair update : pendingUpdates) {
            SyndEntry entry = new SyndEntryImpl();
            entry.setTitle(update.getName());
            String link = update.getValue();

            if (link != null) {
                entry.setLink(link);
            }

            SyndContent content = new SyndContentImpl();
            content.setType("text/html");
            content.setValue(update.getName());

            entry.setDescription(content);
            entries.add(entry);
        }

        feed.setEntries(entries);
        feed.setPublishedDate(entries.isEmpty() ? new Date() : entries.get(0).getPublishedDate());

        return feed;
    }
}
