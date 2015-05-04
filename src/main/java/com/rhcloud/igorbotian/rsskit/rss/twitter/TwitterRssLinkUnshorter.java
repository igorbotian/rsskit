package com.rhcloud.igorbotian.rsskit.rss.twitter;

import com.rhcloud.igorbotian.rsskit.rss.RssModifier;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterRssLinkUnshorter implements RssModifier {

    @Override
    public void apply(SyndFeed feed) {
        Objects.requireNonNull(feed);

        for(SyndEntry entry : feed.getEntries()) {
            updateEntry(entry);
        }
    }

    private void updateEntry(SyndEntry entry) {
        assert entry != null;

        for(SyndEnclosure enclosure : entry.getEnclosures()) {
            updateEnclosure(enclosure);
        }
    }

    private void updateEnclosure(SyndEnclosure enclosure) {
        assert enclosure != null;

        try {
            String unshortenURL = unshortURL(enclosure.getUrl());
            enclosure.setUrl(unshortenURL);
        } catch (IOException e) {
            // skipping this link
        }
    }

    private String unshortURL(String url) throws IOException {
        assert url != null;

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

        for (String header : conn.getHeaderFields().keySet()) {
            if ("Location".equals(header)) {
                return conn.getHeaderField(header);
            }
        }

        return conn.getURL().toString();
    }
}
