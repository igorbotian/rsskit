package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rss.InstapaperBasedRssDescriptionExtender;
import com.rhcloud.igorbotian.rsskit.rss.RssDescriptionExtender;
import com.rhcloud.igorbotian.rsskit.rss.RssModifier;
import com.rhcloud.igorbotian.rsskit.rss.novayagazeta.NovayaGazetaRssFeedModifier;
import com.rometools.rome.feed.synd.SyndFeed;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class NovayaGazetaServlet extends RssFilteringServlet {

    private static final NovayaGazetaRssFeedModifier rssModifier = new NovayaGazetaRssFeedModifier();
    private static final RssDescriptionExtender descriptionExtender = new InstapaperBasedRssDescriptionExtender();

    public NovayaGazetaServlet() throws MalformedURLException {
        super(new URL("http://www.novayagazeta.ru/rss/all.xml"), new RssModifier() {

            @Override
            public void apply(SyndFeed feed) {
                Objects.requireNonNull(feed);

                rssModifier.apply(feed);
                descriptionExtender.apply(feed);
            }
        });
    }
}
