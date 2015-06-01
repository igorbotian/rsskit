package com.rhcloud.igorbotian.rsskit.rss.championat;

import com.rhcloud.igorbotian.rsskit.rss.LinkMapper;
import com.rhcloud.igorbotian.rsskit.rss.RssModifier;
import com.rhcloud.igorbotian.rsskit.utils.RSSUtils;
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
public class ChampionatRssLinkMobilizer implements RssModifier {

    private static final LinkMapper mobileVersionLinkMapper = new MobileVersionLinkMapper();

    @Override
    public void apply(SyndFeed feed) {
        Objects.requireNonNull(feed);
        RSSUtils.mapLinks(feed, mobileVersionLinkMapper);
    }

    private static class MobileVersionLinkMapper implements LinkMapper {

        private static final String M_CHAMPIONAT_COM = "http://m.championat.com";
        private static final Pattern DESKTOP_VERSION_NEWS_URL_FORMAT = Pattern.compile(
                ".*\\.championat\\.com/.*/(\\w+)-(\\d+)-.*"
        );

        @Override
        public URL map(URL url) throws URISyntaxException, MalformedURLException {
            Objects.requireNonNull(url);

            Matcher matcher = DESKTOP_VERSION_NEWS_URL_FORMAT.matcher(url.toString());

            if (!matcher.matches() || matcher.groupCount() < 1) {
                return url;
            }

            String item = matcher.group(1);
            String id = matcher.group(2);

            return new URL(M_CHAMPIONAT_COM + "/" + item + "/sport/football/" + id);
        }
    }
}
