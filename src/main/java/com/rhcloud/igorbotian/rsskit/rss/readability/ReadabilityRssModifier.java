package com.rhcloud.igorbotian.rsskit.rss.readability;

import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizers;
import com.rhcloud.igorbotian.rsskit.rss.RssDescriptionExtender;
import com.rhcloud.igorbotian.rsskit.rss.RssModifier;
import com.rhcloud.igorbotian.rsskit.rss.RssTruncater;
import com.rometools.rome.feed.synd.SyndFeed;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class ReadabilityRssModifier implements RssModifier {

    private static final int MAX_ENTRIES = 1;

    private static final RssTruncater feedTruncater = new RssTruncater(MAX_ENTRIES);
    private static final ReadabilityRssLinksTruncater linkTruncater = new ReadabilityRssLinksTruncater();
    private static final RssDescriptionExtender descriptionExtender = new RssDescriptionExtender(Mobilizers.instapaper());

    @Override
    public void apply(SyndFeed feed) {
        Objects.requireNonNull(feed);

        feedTruncater.apply(feed);
        linkTruncater.apply(feed);
        descriptionExtender.apply(feed);
    }
}
