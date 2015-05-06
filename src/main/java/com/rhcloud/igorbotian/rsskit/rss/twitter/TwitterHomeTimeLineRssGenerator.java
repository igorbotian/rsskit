package com.rhcloud.igorbotian.rsskit.rss.twitter;

import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterTimeline;
import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterTweet;
import com.rhcloud.igorbotian.rsskit.rss.RssGenerator;
import com.rhcloud.igorbotian.rsskit.rss.instagram.InstagramEnclosureExpander;
import com.rometools.rome.feed.synd.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterHomeTimelineRssGenerator extends RssGenerator<TwitterTimeline> {

    private static final String AUTHOR_FORMAT = "%s (%s)";
    private static final String LINK_FORMAT = "https://twitter.com/%s/status/%s";

    private static final TwitterRssDescriptionExtender descriptionExtender = new TwitterRssDescriptionExtender();
    private static final TwitterRssLinkExtractor linkExtractor = new TwitterRssLinkExtractor();
    private static final TwitterRssLinkUnshorter linkUnshorter = new TwitterRssLinkUnshorter();
    private final InstagramEnclosureExpander instagramEnclosureExpander;

    public TwitterHomeTimelineRssGenerator() {
        this(null);
    }

    public TwitterHomeTimelineRssGenerator(String instagramAccessToken) {
        this.instagramEnclosureExpander = StringUtils.isNotEmpty(instagramAccessToken)
                ? new InstagramEnclosureExpander(instagramAccessToken) : null;
    }

    @Override
    public SyndFeed generate(TwitterTimeline timeline) {
        Objects.requireNonNull(timeline);

        SyndFeed feed = skeleton();
        feed.setEntries(generateEntries(timeline.tweets));

        linkExtractor.apply(feed);
        linkUnshorter.apply(feed);

        if(instagramEnclosureExpander != null) {
            instagramEnclosureExpander.apply(feed);
        }

        descriptionExtender.apply(feed);

        return feed;
    }

    @Override
    protected SyndFeed skeleton() {
        SyndFeed feed = new SyndFeedImpl();

        feed.setLink("http://www.twitter.com");
        feed.setPublishedDate(new Date());
        feed.setTitle("Home Timeline");
        feed.setDescription("Twitter Home Timeline");
        feed.setFeedType("rss_2.0");

        return feed;
    }

    private List<SyndEntry> generateEntries(List<TwitterTweet> tweets) {
        assert tweets != null;

        List<SyndEntry> entries = new ArrayList<>(tweets.size());

        for (TwitterTweet tweet : tweets) {
            entries.add(generateEntry(tweet));
        }

        return entries;
    }

    private SyndEntry generateEntry(TwitterTweet tweet) {
        assert tweet != null;

        SyndEntry entry = new SyndEntryImpl();
        entry.setTitle(tweet.text);
        entry.setLink(String.format(LINK_FORMAT, tweet.author.screenName, tweet.id));
        entry.setPublishedDate(tweet.date);
        entry.setAuthor(author(tweet.author.name, tweet.author.screenName));

        SyndContent description = new SyndContentImpl();
        description.setValue(tweet.text);
        description.setType("text/plain");
        entry.setDescription(description);

        List<SyndEnclosure> enclosures = new ArrayList<>(tweet.attachments.size());

        for (String url : tweet.attachments) {
            SyndEnclosure enclosure = new SyndEnclosureImpl();
            enclosure.setUrl(url);

            enclosures.add(enclosure);
        }

        entry.setEnclosures(enclosures);

        return entry;
    }

    private String author(String name, String screenName) {
        return String.format(AUTHOR_FORMAT, name, screenName);
    }
}
