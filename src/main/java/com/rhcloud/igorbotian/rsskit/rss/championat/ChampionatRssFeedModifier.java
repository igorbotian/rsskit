package com.rhcloud.igorbotian.rsskit.rss.championat;

import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizers;
import com.rhcloud.igorbotian.rsskit.rss.LinkMapper;
import com.rhcloud.igorbotian.rsskit.rss.RssModifier;
import com.rhcloud.igorbotian.rsskit.utils.RSSUtils;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class ChampionatRssFeedModifier implements RssModifier {

    private static final LinkMapper mobileVersionLinkMapper = new MobileVersionLinkMapper();

    @Override
    public void apply(SyndFeed feed) {
        Objects.requireNonNull(feed);

        RSSUtils.filterByCategories(feed, Collections.singleton("breaking"));
        RSSUtils.mapLinks(feed, mobileVersionLinkMapper);
        extendDescription(feed);
    }

    private void extendDescription(SyndFeed feed) {
        assert feed != null;

        for(SyndEntry entry : feed.getEntries()) {
            String description;

            try {
                description = getExtendedDescription(entry);
            } catch (IOException e) {
                try {
                    description = Mobilizers.readability().mobilize(new URL(entry.getLink()));
                } catch (IOException ex) {
                    description = entry.getDescription().getValue();
                }
            }

            SyndContent content = new SyndContentImpl();
            content.setType("text/html");
            content.setValue(description);
            entry.setDescription(content);
        }
    }

    private String getExtendedDescription(SyndEntry entry) throws IOException {
        assert entry != null;

        Document html = Jsoup.connect(entry.getLink()).get();
        Elements articleBodies = html.select("div[class=article__text]");

        if(!articleBodies.isEmpty()) {
            return articleBodies.get(0).html();
        }

        throw new IOException("No article text found on a given page: " + entry.getLink());
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
