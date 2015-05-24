package com.rhcloud.igorbotian.rsskit.rss.twitter;

import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterTimeline;
import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterTweet;
import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterURL;
import com.rhcloud.igorbotian.rsskit.rss.InstapaperBasedRssDescriptionExtender;
import com.rhcloud.igorbotian.rsskit.rss.RssDescriptionExtender;
import com.rhcloud.igorbotian.rsskit.rss.RssGenerator;
import com.rhcloud.igorbotian.rsskit.utils.URLUtils;
import com.rometools.rome.feed.synd.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterReadingListRssGenerator extends RssGenerator<TwitterTimeline> {

    private static final Logger LOGGER = LogManager.getLogger(TwitterReadingListRssGenerator.class);
    private final RssDescriptionExtender descriptionExtender = new InstapaperBasedRssDescriptionExtender();

    @Override
    public SyndFeed generate(TwitterTimeline timeline) {
        Objects.requireNonNull(timeline);

        SyndFeed feed = skeleton();
        feed.setEntries(generateEntries(timeline));
        descriptionExtender.apply(feed);

        return feed;
    }

    @Override
    protected SyndFeed skeleton() {
        SyndFeed feed = new SyndFeedImpl();

        feed.setLink("http://www.instapaper.com");
        feed.setPublishedDate(new Date());
        feed.setTitle("Reading list");
        feed.setDescription("Twitter-based reading list");
        feed.setFeedType("rss_2.0");

        return feed;
    }

    private List<SyndEntry> generateEntries(TwitterTimeline timeline) {
        assert timeline != null;

        List<SyndEntry> entries = new ArrayList<>(timeline.tweets.size());

        for(TwitterTweet tweet : timeline.tweets) {
            SyndEntry entry = generateEntry(tweet);

            if(entry != null) {
                entries.add(entry);
            }
        }

        return entries;
    }

    private SyndEntry generateEntry(TwitterTweet tweet) {
        Objects.requireNonNull(tweet);

        if(tweet.entities.urls.isEmpty()) {
            return null;
        }

        TwitterURL url = tweet.entities.urls.get(0);
        String text = getEntryTitle(tweet, url);

        SyndEntry entry = new SyndEntryImpl();

        entry.setTitle(text);
        entry.setLink(url.expandedURL);

        SyndContent description = new SyndContentImpl();
        description.setType("text/html");
        description.setValue(text);

        entry.setDescription(description);

        return entry;
    }

    private String getEntryTitle(TwitterTweet tweet, TwitterURL url) {
        assert tweet != null;
        assert url != null;

        String text = tweet.text.substring(0, url.indices[0]);

        if(StringUtils.isEmpty(text)) {
            try {
                text = URLUtils.getDocumentTitle(new URL(url.expandedURL));
            } catch (IOException e) {
                LOGGER.warn("Failed to get the title of a specified web-page: " + url.expandedURL, e);
            }
        }

        return text;
    }
}
