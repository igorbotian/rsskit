package com.rhcloud.igorbotian.rsskit.rss.twitter;

import com.rhcloud.igorbotian.rsskit.rss.RssModifier;
import com.rometools.rome.feed.synd.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterRssLinkExtractor implements RssModifier {

    @Override
    public void apply(SyndFeed feed) {
        Objects.requireNonNull(feed);

        for(SyndEntry entry : feed.getEntries()) {
            extractLink(entry);
        }
    }

    private void extractLink(SyndEntry entry) {
        assert entry != null;

        NameValuePair descriptionAndLink = extractDescriptionAndLink(entry.getDescription().getValue());
        String description = descriptionAndLink.getName();
        String link = descriptionAndLink.getValue();

        if(StringUtils.isNotEmpty(description)) {
            if(entry.getTitle().equals(entry.getDescription().getValue())) {
                entry.setTitle(description);
            }

            entry.getDescription().setValue(description);
        }

        if(StringUtils.isNotEmpty(link)) {
            List<SyndEnclosure> enclosures = new ArrayList<>();
            SyndEnclosure enclosure = new SyndEnclosureImpl();
            enclosure.setUrl(link);

            enclosures.add(enclosure);
            enclosures.addAll(entry.getEnclosures());
            entry.setEnclosures(enclosures);
        }
    }

    private NameValuePair extractDescriptionAndLink(String text) {
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
}
