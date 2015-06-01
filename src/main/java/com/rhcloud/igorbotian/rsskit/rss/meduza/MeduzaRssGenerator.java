package com.rhcloud.igorbotian.rsskit.rss.meduza;

import com.rhcloud.igorbotian.rsskit.rest.meduza.MeduzaDocument;
import com.rhcloud.igorbotian.rsskit.rss.RssGenerator;
import com.rometools.rome.feed.synd.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class MeduzaRssGenerator extends RssGenerator<List<MeduzaDocument>> {

    private static final String HTML_MIME_TYPE = "text/html";
    private static final String MEDUZA_IO = "https://meduza.io";

    @Override
    public SyndFeed generate(List<MeduzaDocument> documents) {
        Objects.requireNonNull(documents);

        SyndFeed feed = skeleton();
        List<SyndEntry> entries = new ArrayList<>(documents.size());

        for (MeduzaDocument document : documents) {
            entries.add(generateEntry(document));
        }

        feed.setEntries(entries);

        return feed;
    }

    @Override
    protected SyndFeed skeleton() {
        SyndFeed feed = new SyndFeedImpl();

        feed.setTitle("Meduza.io");
        feed.setLink("https://meduza.io");
        feed.setDescription("");
        feed.setFeedType("rss_2.0");

        SyndImage image = new SyndImageImpl();
        image.setTitle(feed.getTitle());
        image.setUrl("https://meduza.io/images/rss-logo.png");
        feed.setImage(image);

        return feed;
    }

    private SyndEntry generateEntry(MeduzaDocument document) {
        assert document != null;

        SyndEntry entry = new SyndEntryImpl();

        entry.setLink(document.url);
        entry.setTitle(document.title);
        entry.setPublishedDate(document.publishedAt);

        SyndContent description = new SyndContentImpl();
        description.setType(HTML_MIME_TYPE);
        description.setValue(generateDescription(document));
        entry.setDescription(description);

        return entry;
    }

    private String generateDescription(MeduzaDocument document) {
        assert document != null;

        StringBuilder description = new StringBuilder();

        if(StringUtils.isNotEmpty(document.imageURL)) {
            if(description.length() > 0) {
                description.append("<br/>");
            }

            String prefix = document.imageURL.startsWith("/") ? MEDUZA_IO : MEDUZA_IO + "/";
            description.append(String.format("<img src='%s'/>", prefix + document.imageURL));

            if(StringUtils.isNotEmpty(document.imageCaption)) {
                description.append("<br/>");
                description.append(document.imageCaption);
            }
        }

        if(StringUtils.isNotEmpty(document.body)) {
            if(description.length() > 0) {
                description.append("<br/>");
            }

            description.append(document.body);
        }

        return description.toString();
    }
}
