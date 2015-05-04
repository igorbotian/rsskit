package com.rhcloud.igorbotian.rsskit.rss.twitter;

import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterException;
import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterTimeline;
import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterTweet;
import com.rometools.rome.feed.synd.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterRssGenerator {

    private static final String AUTHOR_FORMAT = "%s (%s)";
    private static final String LINK_FORMAT = "https://twitter.com/%s/status/%s";

    public SyndFeed generate(TwitterTimeline timeline) throws TwitterException {
        Objects.requireNonNull(timeline);

        SyndFeed feed = new SyndFeedImpl();
        feed.setLink("http://www.twitter.com");
        feed.setPublishedDate(new Date());
        feed.setTitle("Home Timeline");
        feed.setDescription("Twitter Home Timeline");
        feed.setFeedType("rss_2.0");

        feed.setEntries(generateEntries(timeline.tweets));

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

        String author = author(tweet.author.name, tweet.author.screenName);
        List<String> attachments = new ArrayList<>();
        attachments.addAll(tweet.attachments);

        NameValuePair titleAndLink = extractTitleAndLink(tweet.text);
        String title = titleAndLink.getName();
        String url = titleAndLink.getValue();

        if (url != null) {
            attachments.add(0, url);
        }

        SyndEntry entry = new SyndEntryImpl();
        entry.setTitle(StringUtils.isNotEmpty(title) ? title : tweet.text);
        entry.setLink(String.format(LINK_FORMAT, tweet.author.screenName, tweet.id));
        entry.setPublishedDate(tweet.date);
        entry.setAuthor(author);

        SyndContent description = new SyndContentImpl();
        description.setValue(extendDescription(title, author, attachments));
        description.setType("text/html");
        entry.setDescription(description);
        entry.setEnclosures(Collections.singletonList(generateEnclosure(tweet.author.profileImageURL)));

        return entry;
    }

    private String author(String name, String screenName) {
        return String.format(AUTHOR_FORMAT, name, screenName);
    }

    private SyndEnclosure generateEnclosure(String url) {
        assert url != null;

        SyndEnclosure enclosure = new SyndEnclosureImpl();
        enclosure.setUrl(url);

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

            enclosure.setType(conn.getContentType());
            enclosure.setLength(conn.getContentLength());
        } catch (IOException e) {
            // skipping type & length
        }

        return enclosure;
    }

    private String extendDescription(String text, String author, List<String> attachments) {
        assert text != null;
        assert author != null;
        assert attachments != null;

        StringBuilder builder = new StringBuilder(text);

        for (String attachment : attachments) {
            if (builder.length() > 0) {
                builder.append("<br/><br/>");
            }

            builder.append(parseAttachment(attachment));
        }

        return StringEscapeUtils.unescapeHtml4(builder.toString());
    }

    private NameValuePair extractTitleAndLink(String text) {
        assert text != null;

        String description = text;
        String link = null;

        int pos = text.indexOf("http");

        if (pos >= 0) {
            description = text.substring(0, pos);
            link = (String) new StringTokenizer(text.substring(pos), " ").nextElement();
        }

        return new BasicNameValuePair(description, link);
    }

    private String parseAttachment(String url) {
        assert url != null;

        String originalURL = url;
        boolean image = false;

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.connect();
            originalURL = getOriginalURL(conn);
            image = isImage(conn);
        } catch (IOException e) {
            // skipping this attachment
        }

        if (image) {
            return String.format("<img src='%s'/>", originalURL);
        }

        return String.format("<a href='%s'/>%s</a>", originalURL, originalURL);
    }

    private String getOriginalURL(HttpURLConnection conn) {
        assert conn != null;

        for (String header : conn.getHeaderFields().keySet()) {
            if ("Location".equals(header)) {
                return conn.getHeaderField(header);
            }
        }

        return conn.getURL().toString();
    }

    private boolean isImage(HttpURLConnection conn) {
        assert conn != null;

        String contentType = conn.getContentType();

        return "image/jpeg".equals(contentType)
                || "image/gif".equals(contentType)
                || "image/png".equals(contentType);
    }
}
