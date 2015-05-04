package com.rhcloud.igorbotian.rsskit.rss.instagram;

import com.rhcloud.igorbotian.rsskit.rest.instagram.InstagramAPI;
import com.rhcloud.igorbotian.rsskit.rest.instagram.InstagramAPIImpl;
import com.rhcloud.igorbotian.rsskit.rest.instagram.InstagramException;
import com.rhcloud.igorbotian.rsskit.rss.RssModifier;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class InstagramEnclosureExpander implements RssModifier {

    private final String accessToken;
    private final InstagramAPI api = new InstagramAPIImpl();

    public InstagramEnclosureExpander(String instagramAccessToken) {
        this.accessToken = Objects.requireNonNull(instagramAccessToken);
    }

    @Override
    public void apply(SyndFeed feed) {
        Objects.requireNonNull(feed);

        if (StringUtils.isNotEmpty(accessToken)) {
            for (SyndEntry entry : feed.getEntries()) {
                expandEntry(entry);
            }
        }
    }

    private void expandEntry(SyndEntry entry) {
        assert entry != null;

        for(SyndEnclosure enclosure : entry.getEnclosures()) {
            updateEnclosure(enclosure);
        }
    }

    private void updateEnclosure(SyndEnclosure enclosure) {
        assert enclosure != null;

        try {
            URL url = new URL(enclosure.getUrl());

            if (api.isShortenURL(url)){
                URL unshorten = api.unshortURL(url, accessToken);
                enclosure.setUrl(unshorten.toString());
            }
        } catch (MalformedURLException | InstagramException e) {
            // skipping this enclosure
        }
    }
}
