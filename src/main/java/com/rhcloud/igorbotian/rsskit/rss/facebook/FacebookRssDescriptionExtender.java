package com.rhcloud.igorbotian.rsskit.rss.facebook;

import com.rhcloud.igorbotian.rsskit.rss.RssModifier;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class FacebookRssDescriptionExtender implements RssModifier {

    private static final Logger LOGGER = LogManager.getLogger(FacebookRssDescriptionExtender.class);
    private static final String HTML_MIME_TYPE = "text/html";
    private static final String USER_AGENT = "mozilla/5.0 (iphone; cpu iphone os 7_0_2 like mac os x) " +
            "applewebkit/537.51.1 (khtml, like gecko) version/7.0 mobile/11a501 safari/9537.53";
    private static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
    private static final String FACEBOOK_DOMAIN = "facebook.com";
    private static final String MOBILE_FACEBOOK_HOST = "m.facebook.com";

    @Override
    public void apply(SyndFeed feed) {
        Objects.requireNonNull(feed);

        for (SyndEntry entry : feed.getEntries()) {
            extendDescription(entry);
        }
    }

    private void extendDescription(SyndEntry entry) {
        assert entry != null;

        try {
            URL url = new URL(entry.getLink());

            if (isFacebookPostURL(url)) {
                URL mobileURL = changeToMobileVersion(url);
                extendDescription(entry, mobileURL);
            }
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.error("Failed to mobilize Facebook post URL: " + entry.getLink());
        } catch (IOException e) {
            LOGGER.warn("Unable to extend Facebook post description: " + entry.getLink());
        }
    }

    private boolean isFacebookPostURL(URL url) {
        assert url != null;
        return url.getHost().endsWith(FACEBOOK_DOMAIN);
    }

    private URL changeToMobileVersion(URL fbURL) throws MalformedURLException, URISyntaxException {
        assert fbURL != null;

        URIBuilder builder = new URIBuilder(fbURL.toURI());
        builder.setHost(MOBILE_FACEBOOK_HOST);
        return builder.build().toURL();
    }

    private void extendDescription(SyndEntry entry, URL fbURL) throws IOException {
        assert entry != null;
        assert fbURL != null;

        Document fbPage = downloadFacebookPost(fbURL);
        String description = extendDescription(fbPage);

        if (StringUtils.isNotEmpty(description)) {
            SyndContent content = new SyndContentImpl();
            content.setType(HTML_MIME_TYPE);
            content.setValue(description);

            entry.setDescription(content);
        }
    }

    private String extendDescription(Document fbPage) {
        assert fbPage != null;

        StringBuilder description = new StringBuilder();
        Elements storyDivs = fbPage.select("div[data-sigil*=story-div]");

        if(!storyDivs.isEmpty()) {
            Element storyDiv = storyDivs.get(0);
            String repost = extractRepost(storyDiv);

            if(StringUtils.isNotEmpty(repost)) {
                description.append(repost);
            } else {
                description.append(extractPost(storyDiv));
            }
        }

        return description.toString();
    }

    private String extractRepost(Element storyDiv) {
        assert storyDiv != null;

        Elements tags = storyDiv.select("article");

        if(tags.isEmpty()) {
            return "";
        }

        Element article = tags.get(0);
        return isNotFromComments(article) ? article.html() : "";
    }

    private String extractPost(Element storyDiv) {
        assert storyDiv != null;

        StringBuilder post = new StringBuilder();
        Elements tags = storyDiv.select("[data-sigil]");

        for(int i = 0; i < tags.size(); i++) {
            Element tag = tags.get(i);
            String dataSigil = tag.attr("data-sigil");

            if(dataSigil.equals("mfeed_pivots_message") || dataSigil.contains("mfeed_pivots_attachment")) {
                if(isNotFromComments(tag)) {
                    if (post.length() > 0) {
                        post.append("<br/>");
                    }

                    post.append(tag.html());
                }
            }
        }

        return post.toString();
    }

    private boolean isNotFromComments(Element tag) {
        assert tag != null;

        while(tag != null) {
            String dataSigil = tag.attr("data-sigil");

            if(StringUtils.equals(dataSigil, "m-mentions-expand")
                    || StringUtils.equals(dataSigil, "comment")) {
                return false;
            }

            tag = tag.parent();
        }

        return true;
    }

    private Document downloadFacebookPost(URL url) throws IOException {
        assert url != null;

        Connection conn = Jsoup.connect(url.toString());
        conn = conn.header("User-Agent", USER_AGENT);
        conn = conn.header("Accept", ACCEPT);

        return conn.get();
    }
}
