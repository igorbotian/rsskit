package com.rhcloud.igorbotian.rsskit.rss;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public abstract class RssDescriptionExtender implements RssModifier {

    private static final Logger LOGGER = LogManager.getLogger(RssDescriptionExtender.class);
    protected static final String HTTP_MIME_TYPE = "text/html";

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
                LOGGER.warn("Failed to extend RSS entry description by a specified link: " + entry.getLink(), e);
            }
        }
    }

    private void extendDescription(SyndEntry entry) throws IOException {
        assert entry != null;

        String fullDescription = mobilize(new URL(entry.getLink()));
        SyndContent content = new SyndContentImpl();
        content.setType(HTTP_MIME_TYPE);
        content.setValue(fullDescription);

        entry.setDescription(content);
    }

    protected abstract String mobilize(URL url) throws IOException;
}
