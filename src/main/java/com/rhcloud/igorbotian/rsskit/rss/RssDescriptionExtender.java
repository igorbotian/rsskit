package com.rhcloud.igorbotian.rsskit.rss;

import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizer;
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
public class RssDescriptionExtender implements RssModifier {

    protected static final String HTTP_MIME_TYPE = "text/html";

    private final Mobilizer mobilizer;

    public RssDescriptionExtender(Mobilizer mobilizer) {
        this.mobilizer = Objects.requireNonNull(mobilizer);
    }

    @Override
    public void apply(SyndFeed feed) {
        Objects.requireNonNull(feed);
        extendDescription(feed);
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
