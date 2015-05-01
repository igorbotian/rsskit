package com.rhcloud.igorbotian.rsskit.rss.championat;

import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizers;
import com.rhcloud.igorbotian.rsskit.rss.LinkMapper;
import com.rhcloud.igorbotian.rsskit.rss.RssDescriptionExtender;
import com.rhcloud.igorbotian.rsskit.rss.RssLinkMapper;
import com.rhcloud.igorbotian.rsskit.rss.RssModifier;
import com.rometools.rome.feed.synd.SyndFeed;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class ChampionatRssFeedModifier implements RssModifier {

    private static final RssModifier breakingNewsFilter = new BreakingNewsFilter();
    private static final RssLinkMapper mobileVersionLinkMapper = new RssLinkMapper(new MobileVersionLinkMapper());
    private static final RssModifier descriptionExtender = new RssDescriptionExtender(Mobilizers.instapaper());

    @Override
    public void apply(SyndFeed feed) {
        Objects.requireNonNull(feed);

        breakingNewsFilter.apply(feed);
        mobileVersionLinkMapper.apply(feed);
        descriptionExtender.apply(feed);
    }

    private static class MobileVersionLinkMapper implements LinkMapper {

        private static final Pattern DESKTOP_VERSION_NEWS_URL_FORMAT = Pattern.compile(".*\\.championat\\.com/.*/news-(\\d+)-.*");
        private static final String MOBILE_VERSION_NEWS_URL_PREFIX = "http://m.championat.com/news/sport/football/";

        @Override
        public URL map(URL url) throws URISyntaxException, MalformedURLException {
            Objects.requireNonNull(url);

            Matcher matcher = DESKTOP_VERSION_NEWS_URL_FORMAT.matcher(url.toString());

            if (!matcher.matches() || matcher.groupCount() < 1) {
                return url;
            }

            return new URL(MOBILE_VERSION_NEWS_URL_PREFIX + matcher.group(1));
        }
    }
}
