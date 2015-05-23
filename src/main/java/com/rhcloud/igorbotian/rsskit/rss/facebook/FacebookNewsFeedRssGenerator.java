package com.rhcloud.igorbotian.rsskit.rss.facebook;

import com.rhcloud.igorbotian.rsskit.rest.facebook.*;
import com.rhcloud.igorbotian.rsskit.rss.RssGenerator;
import com.rometools.rome.feed.synd.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class FacebookNewsFeedRssGenerator extends RssGenerator<FacebookNewsFeed> {

    private static final Logger LOGGER = LogManager.getLogger(FacebookNewsFeedRssGenerator.class);
    private static final String HTML_MIME_TYPE = "text/html";
    private static final Pattern URL_REGEX = Pattern.compile("\\b(https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])");
    private static final int MAX_URL_LENGTH = 40;

    @Override
    public SyndFeed generate(FacebookNewsFeed newsFeed) {
        Objects.requireNonNull(newsFeed);

        SyndFeed rss = skeleton();
        List<SyndEntry> entries = new ArrayList<>(newsFeed.posts.size());

        for(FacebookNewsFeedItem post : newsFeed.posts) {
            entries.add(generateEntry(post));
        }

        rss.setEntries(entries);

        return rss;
    }

    @Override
    protected SyndFeed skeleton() {
        SyndFeed rss = new SyndFeedImpl();

        rss.setTitle("Facebook");
        rss.setPublishedDate(new Date());
        rss.setDescription("Facebook News Feed");
        rss.setLink("http://www.facebook.com");
        rss.setFeedType("rss_2.0");

        return rss;
    }

    private SyndEntry generateEntry(FacebookNewsFeedItem item) {
        assert item != null;

        if(item instanceof FacebookRepost) {
            FacebookRepost repost = (FacebookRepost) item;
            return generateEntry(repost.from, repost.repost);
        }

        assert item instanceof FacebookPost;
        FacebookPost post = (FacebookPost) item;
        return generateEntry(post.from, post);
    }

    private SyndEntry generateEntry(FacebookProfile from, FacebookPost post) {
        assert from != null;
        assert post != null;

        SyndEntry entry = new SyndEntryImpl();

        entry.setAuthor(post.from.name);
        entry.setLink("https://www.facebook.com/" + post.id);
        entry.setTitle(post.from.name);
        entry.setPublishedDate(post.createdTime);
        entry.setDescription(generateDescription(from, post));

        return entry;
    }

    private SyndContent generateDescription(FacebookProfile from, FacebookPost post) {
        assert from != null;
        assert post != null;

        StringBuilder description = new StringBuilder();

        if(!StringUtils.equals(from.id, post.from.id)) {
            description.append(repostCaption(from));
            description.append("<br/><br/>");
        }

        description.append(generateDescription(post));

        SyndContent content = new SyndContentImpl();
        content.setType(HTML_MIME_TYPE);
        content.setValue(description.toString());

        return content;
    }

    private String generateDescription(FacebookPost post) {
        assert post != null;

        StringBuilder description = new StringBuilder();

        if(StringUtils.isNotEmpty(post.message)) {
            description.append(html(post.message));
        }

        String attachment = generateAttachment(post);

        if(description.length() > 0 && attachment.length() > 0) {
            description.append("<br/></br>");
        }

        description.append(attachment);
        return description.toString();
    }

    private String generateAttachment(FacebookPost post) {
        assert post != null;

        String content = "";

        switch (post.type) {
            case OFFER:
                content = processOffer((FacebookOffer) post);
                break;
            case VIDEO:
                content = processVideo((FacebookVideo) post);
                break;
            case PHOTO:
                content = processPhoto((FacebookPhoto) post);
                break;
            case LINK:
                content = processLink((FacebookLink) post);
                break;
            case STATUS:
                content = processStatus((FacebookStatus) post);
                break;
        }

        return content;
    }

    private String repostCaption(FacebookProfile from) {
        assert from != null;

        return String.format(
                "<a href='%s' style='text-decoration: none'>&#8618;&nbsp;<b>%s</b></a>",
                from.link,
                from.name
        );
    }

    private String processLink(FacebookLink link) {
        assert link != null;

        StringBuilder content = new StringBuilder();

        if(StringUtils.isNotEmpty(link.picture)) {
            content.append(String.format("<a href='%s'>", link.link));
            content.append(String.format("<img src='%s'/>", link.picture));
            content.append("</a>");
        }

        if(StringUtils.isNotEmpty(link.name)) {
            if(content.length() > 0) {
                content.append("<br/>");
            }

            content.append(String.format("<b>%s</b>", link.name));
        }

        if(StringUtils.isNotEmpty(link.description)) {
            if(content.length() > 0) {
                content.append("<br/>");
            }

            content.append("<font style='font-size: small'>");
            content.append(html(link.description));
            content.append("</font>");
        }

        if(content.length() > 0) {
            content.append("<br/>");
        }

        content.append(String.format(
                "<a href='%s'><font style='text-decoration: none; font-size: small'>%s</font></a>",
                link.link,
                StringUtils.isNotEmpty(link.caption) ? link.caption : shortenLink(link.link)
        ));

        return content.toString();
    }

    private String processStatus(FacebookStatus status) {
        assert status != null;
        return ""; // nothing additional
    }

    private String processPhoto(FacebookPhoto photo) {
        assert photo != null;

        StringBuilder content = new StringBuilder();

        if(StringUtils.isNotEmpty(photo.name)) {
            content.append(html(photo.name));
        }

        if(content.length() > 0) {
            content.append("<br/><br/>");
        }

        content.append(String.format("<a href='%s'><img src='%s'/></a>",
                photo.link, StringUtils.isNotEmpty(photo.image) ? photo.image : photo.picture));

        return content.toString();
    }

    private String processVideo(FacebookVideo video) {
        assert video != null;

        StringBuilder content = new StringBuilder();

        if(StringUtils.isNotEmpty(video.embedHTML)) {
            content.append(video.embedHTML);
        } else {
            content.append(String.format("<a href='%s'><img src='%s'/></a>", video.source, video.picture));
        }

        if(StringUtils.isNotEmpty(video.name)) {
            if(content.length() > 0) {
                content.append("<br/>");
            }

            content.append(String.format("<b>%s</b>", html(video.name)));
        }

        if(StringUtils.isNotEmpty(video.description)) {
            if(content.length() > 0) {
                content.append("<br/>");
            }

            content.append(String.format("<font style='font-size: small'>%s</font>", html(video.description)));
        }

        if(StringUtils.isNotEmpty(video.caption)) {
            if(content.length() > 0) {
                content.append("<br/>");
            }

            content.append(String.format("<font style='font-size: small'><i>%s</i></font>", html(video.caption)));
        }

        return content.toString();
    }

    private String processOffer(FacebookOffer offer) {
        assert offer != null;
        return ""; // nothing additional
    }

    private String shortenLink(String url) {
        assert url != null;

        if(url.length() > MAX_URL_LENGTH) {
            try {
                URIBuilder builder = new URIBuilder(url);
                return String.format("%s://%s/", builder.getScheme(), builder.getHost());
            } catch (URISyntaxException e) {
                LOGGER.warn("Failed to shorten a specified link: " + url, e);
            }
        }

        return url;
    }

    private String html(String text) {
        assert text != null;

        String html = StringUtils.replace(text, "\n", "<br/>");

        for(String link : findLinks(html)) {
            html = StringUtils.replace(
                    html,
                    link, String.format("<a href='%s'><font style='text-decoration: none'>%s</font></a>", link, link)
            );
        }

        return html;
    }

    private Set<String> findLinks(String text) {
        assert text != null;

        Set<String> links = new LinkedHashSet<>();
        Matcher matcher = URL_REGEX.matcher(text);

        while(matcher.find()) {
            links.add(text.substring(matcher.start(0), matcher.end(0)));
        }

        return links;
    }
}
