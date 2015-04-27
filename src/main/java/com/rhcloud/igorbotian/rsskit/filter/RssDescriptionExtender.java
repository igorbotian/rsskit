package com.rhcloud.igorbotian.rsskit.filter;

import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizer;
import com.rhcloud.igorbotian.rsskit.utils.RssFeedUtils;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class RssDescriptionExtender implements RssFilter {

    protected static final String HTTP_MIME_TYPE = "text/html";

    private final Mobilizer mobilizer;

    public RssDescriptionExtender(Mobilizer mobilizer) {
        this.mobilizer = Objects.requireNonNull(mobilizer);
    }

    @Override
    public SyndFeed apply(SyndFeed original) {
        Objects.requireNonNull(original);

        try {
            SyndFeed feed = RssFeedUtils.clone(original);
            extendDescription(feed);
            return feed;
        } catch (IOException e) {
            return original;
        }
    }

    private void extendDescription(SyndFeed feed) {
        assert feed != null;

        for (SyndEntry entry : feed.getEntries()) {
            try {
                extendDescription(entry);
            } catch (IOException e) {
                e.printStackTrace(); // skip this description
            }
        }
    }

    private void extendDescription(SyndEntry entry) throws IOException {
        assert entry != null;

        String fullDescription = mobilizer.mobilize(new URL(entry.getLink()));
        SyndContent content = new SyndContentImpl();
        content.setType(HTTP_MIME_TYPE);
        content.setValue(fullDescription);

        entry.setDescription(content);
    }
}
