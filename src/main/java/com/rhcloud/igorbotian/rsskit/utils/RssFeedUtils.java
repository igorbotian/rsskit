package com.rhcloud.igorbotian.rsskit.utils;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.SyndFeedOutput;
import com.rometools.rome.io.XmlReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public final class RssFeedUtils {

    private RssFeedUtils() {
        //
    }

    public static SyndFeed clone(SyndFeed feed) throws IOException {
        Objects.requireNonNull(feed);

        try {
            SyndFeedOutput output = new SyndFeedOutput();
            String rawXML = output.outputString(feed);
            SyndFeedInput input = new SyndFeedInput();
            return input.build(new XmlReader(new ByteArrayInputStream(rawXML.getBytes(StandardCharsets.UTF_8))));
        } catch (FeedException e) {
            throw new IOException("Failed to clone feed: " + feed.getLink(), e);
        }
    }
}
