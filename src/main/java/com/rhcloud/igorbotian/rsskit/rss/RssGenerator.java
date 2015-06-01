package com.rhcloud.igorbotian.rsskit.rss;

import com.rometools.rome.feed.synd.*;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public abstract class RssGenerator<T> {

    protected static final String RSS_20 = "rss_2.0";
    protected static final String HTML_MIME_TYPE = "text/html";
    private static final SimpleDateFormat UTC_FORMAT = new SimpleDateFormat("yyyy-MM-dd KK:mm:ss a Z");

    public SyndFeed error(Exception ex) {
        Objects.requireNonNull(ex);

        SyndFeed skeleton = skeleton();
        SyndFeed rss = new SyndFeedImpl();
        rss.setDescription(skeleton.getDescription());
        rss.setTitle(skeleton.getTitle());
        rss.setLink(skeleton.getLink());
        rss.setPublishedDate(skeleton.getPublishedDate());
        rss.setAuthor(skeleton.getAuthor());
        rss.setFeedType(skeleton.getFeedType());

        List<SyndEntry> entries = new ArrayList<>();
        entries.addAll(skeleton.getEntries());
        entries.add(generateErrorEntry(ex));

        rss.setEntries(entries);

        return rss;
    }

    private SyndEntry generateErrorEntry(Exception ex) {
        assert ex != null;

        SyndEntry entry = new SyndEntryImpl();

        SyndContent description = new SyndContentImpl();
        description.setType("text/html");
        description.setValue(printErrorMessages(ex));

        entry.setTitle("Error (" + UTC_FORMAT.format(new Date()) + ")");
        entry.setDescription(description);
        entry.setPublishedDate(new Date());

        return entry;
    }

    private String printErrorMessages(Exception ex) {
        assert ex != null;

        StringBuilder text = new StringBuilder();

        List<String> messages = gatherErrorMessages(ex);

        for(int i = 0; i < messages.size(); i++) {
            if(i > 0) {
                text.append("<br/>");
                text.append(StringUtils.repeat("&nbsp;&nbsp;", i));
                text.append("&#8627;");
                text.append("&nbsp;");
            }

            text.append(messages.get(i));
        }

        return text.toString();
    }

    private List<String> gatherErrorMessages(Exception ex) {
        List<String> messages = new ArrayList<>();
        Throwable it = ex;

        while(it != null) {
            messages.add(it.getMessage());
            it = it.getCause();
        }

        return messages;
    }

    public abstract SyndFeed generate(T obj);

    protected abstract SyndFeed skeleton();
}
