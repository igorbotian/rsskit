package com.rhcloud.igorbotian.rsskit.utils;

import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizer;
import com.rhcloud.igorbotian.rsskit.rss.LinkMapper;
import com.rometools.rome.feed.synd.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class RSSUtils {

    private static final Logger LOGGER = LogManager.getLogger(RSSUtils.class);
    public static final String HTTP_MIME_TYPE = "text/html";

    private RSSUtils() {
        //
    }

    //-------------------------------------------------------------------------

    public static void truncate(SyndFeed feed, int maxEntries) {
        Objects.requireNonNull(feed);

        if(maxEntries < 1) {
            throw new IllegalArgumentException("Maximum entries allowed should have a positive value");
        }

        List<SyndEntry> entries = feed.getEntries();
        int size = Math.min(maxEntries, entries.size());
        feed.setEntries(entries.subList(0, size));
    }

    //-------------------------------------------------------------------------

    public static void mapLinks(SyndFeed feed, LinkMapper mapper) {
        Objects.requireNonNull(feed);
        Objects.requireNonNull(mapper);

        for (SyndEntry entry : feed.getEntries()) {
            try {
                URL link = mapper.map(new URL(entry.getLink()));
                entry.setLink(link.toString());
            } catch (IOException | URISyntaxException e) {
                LOGGER.warn("Failed to map link: " + entry.getLink(), e);
            }
        }
    }

    //-------------------------------------------------------------------------

    public static void extendDescription(SyndFeed feed, Mobilizer mobilizer) {
        Objects.requireNonNull(feed);
        Objects.requireNonNull(mobilizer);

        for (SyndEntry entry : feed.getEntries()) {
            try {
                extendDescription(entry, mobilizer);
            } catch (IOException e) {
                LOGGER.warn("Failed to extend RSS entry description by a given link: " + entry.getLink(), e);
            }
        }
    }

    private static void extendDescription(SyndEntry entry, Mobilizer mobilizer) throws IOException {
        assert entry != null;
        assert mobilizer != null;

        String fullDescription = mobilizer.mobilize(new URL(entry.getLink()));
        SyndContent content = new SyndContentImpl();
        content.setType(HTTP_MIME_TYPE);
        content.setValue(fullDescription);

        entry.setDescription(content);
    }

    //-------------------------------------------------------------------------

    public static void filter(SyndFeed feed, RssEntryFilter filter) {
        Objects.requireNonNull(feed);
        Objects.requireNonNull(filter);

        List<SyndEntry> filtered = new ArrayList<>(feed.getEntries().size());

        for (SyndEntry entry : feed.getEntries()) {
            if (filter.apply(entry)) {
                filtered.add(entry);
            }
        }

        feed.setEntries(filtered);
    }

    //-------------------------------------------------------------------------

    public static void filterByCategories(SyndFeed feed, Set<String> categories) {
        Objects.requireNonNull(feed);
        Objects.requireNonNull(categories);

        if (categories.isEmpty()) {
            return;
        }

        List<SyndEntry> filtered = new ArrayList<>(feed.getEntries().size());

        for (SyndEntry entry : feed.getEntries()) {
            if (hasSpecifiedCategory(entry, categories)) {
                filtered.add(entry);
            }
        }

        feed.setEntries(filtered);
    }

    private static boolean hasSpecifiedCategory(SyndEntry entry, Set<String> categories) {
        assert entry != null;
        assert categories != null;

        for (String requiredCategory : categories) {
            for (SyndCategory category : entry.getCategories()) {
                if (category.getName().equals(requiredCategory)) {
                    return true;
                }
            }
        }

        return false;
    }

    //-------------------------------------------------------------------------
}
