package com.rhcloud.igorbotian.rsskit.rss.buffer;

import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizers;
import com.rhcloud.igorbotian.rsskit.rss.RssDescriptionExtender;
import com.rhcloud.igorbotian.rsskit.rss.RssGenerator;
import com.rometools.rome.feed.synd.*;
import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class BufferPendingUpdatesRssGenerator extends RssGenerator<List<NameValuePair>> {

    private static final RssDescriptionExtender descriptionExtender = new RssDescriptionExtender(Mobilizers.instapaper());

    public SyndFeed generate(List<NameValuePair> pendingUpdates) {
        Objects.requireNonNull(pendingUpdates);

        SyndFeed feed = generateFeed(pendingUpdates);
        descriptionExtender.apply(feed);
        return feed;
    }

    private SyndFeed generateFeed(List<NameValuePair> pendingUpdates) {
        assert pendingUpdates != null;

        SyndFeed feed = skeleton();
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

    @Override
    protected SyndFeed skeleton() {
        SyndFeed feed = new SyndFeedImpl();

        feed.setTitle("Buffer pending updates");
        feed.setFeedType("rss_2.0");
        feed.setDescription("Provides pending updates from different Buffer profiles (Twitter, Facebook, etc.)");
        feed.setLink("http://www.buffer.com");

        return feed;
    }
}
