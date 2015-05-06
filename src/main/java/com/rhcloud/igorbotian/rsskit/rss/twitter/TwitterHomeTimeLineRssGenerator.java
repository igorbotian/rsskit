package com.rhcloud.igorbotian.rsskit.rss.twitter;

import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterMedia;
import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterTimeline;
import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterTweet;
import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterURL;
import com.rhcloud.igorbotian.rsskit.rss.RssGenerator;
import com.rometools.rome.feed.synd.*;
import org.apache.commons.lang3.StringEscapeUtils;
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

    @Override
    public SyndFeed generate(TwitterTimeline timeline) {
        Objects.requireNonNull(timeline);

        SyndFeed feed = skeleton();
        feed.setEntries(generateEntries(timeline.tweets));

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
        entry.setTitle(tweet.author.name);
        entry.setLink(String.format(LINK_FORMAT, tweet.author.screenName, tweet.id));
        entry.setPublishedDate(tweet.date);
        entry.setAuthor(author(tweet.author.name, tweet.author.screenName));

        List<SyndEnclosure> enclosures = new ArrayList<>(tweet.entities.urls.size());

        String text = getDescription(tweet);

        for (TwitterURL url : tweet.entities.urls) {
            text = StringUtils.replace(
                    text,
                    url.mediaURL,
                    String.format("<a href='%s'>%s</a>", url.expandedURL, url.expandedURL)
            );

            SyndEnclosure enclosure = new SyndEnclosureImpl();
            enclosure.setUrl(url.expandedURL);

            enclosures.add(enclosure);
        }

        entry.setEnclosures(enclosures);

        for (TwitterMedia media : tweet.entities.media) {
            if(!text.isEmpty()) {
                text += "<br/><br/>";
            }

            text += String.format("<img src='%s'/>", media.mediaURL);
        }

        SyndContent description = new SyndContentImpl();
        description.setValue(StringEscapeUtils.unescapeHtml4(text));
        description.setType("text/html");

        entry.setDescription(description);

        return entry;
    }

    private String getDescription(TwitterTweet tweet) {
        assert tweet != null;
        return tweetTextWithoutLinksToMedia(tweet);
    }

    private String tweetTextWithoutLinksToMedia(TwitterTweet tweet) {
        assert tweet != null;

        String text = tweet.text;

        for(TwitterMedia media : tweet.entities.media) {
            text = StringUtils.replace(text, media.url, "");
        }

        return text;
    }

    private String author(String name, String screenName) {
        return String.format(AUTHOR_FORMAT, name, screenName);
    }
}
