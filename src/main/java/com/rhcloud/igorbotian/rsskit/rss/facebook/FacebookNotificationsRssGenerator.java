package com.rhcloud.igorbotian.rsskit.rss.facebook;

import com.rhcloud.igorbotian.rsskit.rest.facebook.*;
import com.rhcloud.igorbotian.rsskit.rss.RssGenerator;
import com.rometools.rome.feed.synd.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class FacebookNotificationsRssGenerator extends RssGenerator<FacebookNotifications> {

    private static final String HTML_MIME_TYPE = "text/html";
    private static final Pattern URL_REGEX = Pattern.compile("\\b(https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])");

    @Override
    public SyndFeed generate(FacebookNotifications notifications) {
        Objects.requireNonNull(notifications);

        SyndFeed rss = skeleton();
        List<SyndEntry> entries = new ArrayList<>(notifications.items.size());

        for(FacebookNotification notification : notifications.items) {
            entries.add(generateEntry(notification));
        }

        rss.setEntries(entries);

        return rss;
    }

    @Override
    protected SyndFeed skeleton() {
        SyndFeed rss = new SyndFeedImpl();

        rss.setTitle("Facebook");
        rss.setPublishedDate(new Date());
        rss.setDescription("Facebook notifications feed");
        rss.setLink("http://www.facebook.com");
        rss.setFeedType("rss_2.0");

        return rss;
    }

    private SyndEntry generateEntry(FacebookNotification notification) {
        assert notification != null;

        SyndEntry entry = new SyndEntryImpl();
        entry.setAuthor(notification.from.name);
        entry.setLink(notification.link);
        entry.setTitle(notification.title);
        entry.setPublishedDate(notification.createdTime);
        entry.setDescription(generateDescription(notification.from, notification.post));

        return entry;
    }

    private SyndContent generateDescription(FacebookAuthor author, FacebookPost post) {
        assert author != null;
        assert post != null;

        StringBuilder description = new StringBuilder();

        if(!author.id.equals(post.from.id)) {
            description.append(repostCaption(post));
            description.append("<br/>");
        }

        switch (post.type) {
            case OFFER:
                description.append(processOffer((FacebookOffer) post.object));
                break;
            case VIDEO:
                description.append(processVideo((FacebookVideo) post.object));
                break;
            case PHOTO:
                description.append(processPhoto((FacebookPhoto) post.object));
                break;
            case LINK:
                description.append(processLink((FacebookLink) post.object));
                break;
            case STATUS:
                description.append(processStatus((FacebookStatus) post.object));
                break;
        }

        SyndContent content = new SyndContentImpl();
        content.setType(HTML_MIME_TYPE);
        content.setValue(description.toString());

        return content;
    }

    private String repostCaption(FacebookPost repost) {
        assert repost != null;

        return String.format(
                "<a href='%s' style='text-decoration: none'>&#8618;&nbsp;<b>%s</b></a>",
                repost.from.link,
                repost.from.name
        );
    }

    private String processLink(FacebookLink link) {
        assert link != null;

        StringBuilder content = new StringBuilder();

        if(StringUtils.isNotEmpty(link.message)) {
            content.append(html(link.message));
        }

        if(content.length() > 0) {
            content.append("<br/><br/>");
        }

        content.append("<table>");
        content.append("<tr>");

        if(StringUtils.isNotEmpty(link.picture)) {
            content.append("<td>");
            content.append(String.format("<a href='%s'>", link.link));
            content.append(String.format("<img src='%s'/>", link.picture));
            content.append("</a>");
            content.append("</td>");
        }

        content.append("<td>");

        if(StringUtils.isNotEmpty(link.name)) {
            content.append(String.format("<b>%s</b>", link.name));
        }

        if(StringUtils.isNotEmpty(link.description)) {
            content.append("<br/>");
            content.append("<font style='font-size: small'>");
            content.append(html(link.description));
            content.append("</font>");
        }

        content.append(String.format(
                "<br/><a href='%s'><font style='text-decoration: none; font-size: small'>%s</font></a>",
                link.link,
                StringUtils.isNotEmpty(link.caption) ? link.caption : link.link
        ));

        content.append("</td>");
        content.append("</tr>");
        content.append("</table>");

        return content.toString();
    }

    private String processStatus(FacebookStatus status) {
        assert status != null;

        return StringUtils.isNotEmpty(status.message) ? html(status.message) : "";
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

        content.append(String.format("<a href='%s'><img src='%s'/></a>", photo.link, photo.source));

        return content.toString();
    }

    private String processVideo(FacebookVideo video) {
        assert video != null;

        StringBuilder content = new StringBuilder();

        content.append(html(video.message));

        if(content.length() > 0) {
            content.append("<br/><br/>");
        }

        content.append("<table>");
        content.append("<tr>");

        content.append("<td>");
        content.append(String.format("<a href='%s'><img src='%s'/></a>", video.source, video.picture));
        content.append("</td>");

        content.append("<td>");

        if(StringUtils.isNotEmpty(video.name)) {
            content.append(String.format("<b>%s</b>", html(video.name)));
        }

        if(StringUtils.isNotEmpty(video.description)) {
            content.append(String.format("<br/><font style='font-size: small'>%s</font>", html(video.description)));
        }

        if(StringUtils.isNotEmpty(video.caption)) {
            content.append(String.format("<br/><font style='font-size: small'><i>%s</i></font>", html(video.caption)));
        }

        content.append("</tr>");
        content.append("</table>");

        return content.toString();
    }

    private String processOffer(FacebookOffer offer) {
        assert offer != null;
        return StringUtils.isNotEmpty(offer.message) ? html(offer.message) : "";
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
