package com.rhcloud.igorbotian.rsskit.rss.vk;

import com.rhcloud.igorbotian.rsskit.rest.vk.*;
import com.rhcloud.igorbotian.rsskit.rss.RssGenerator;
import com.rometools.rome.feed.synd.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkNewsFeedRssGenerator extends RssGenerator<VkFeed> {

    private static final String PHOTO_LINK_FORMAT = "https://vk.com/photo%s_%s";
    private static final String VIDEO_LINK_FORMAT = "https://vk.com/video%s_%s";
    private static final String POST_LINK_FORMAT = "https://vk.com/feed?w=wall%s_%s";
    private static final String USER_ID_LINK_FORMAT = "https://vk.com/id%d";
    private static final String CLUB_LINK_FORMAT = "https://vk.com/club%d";
    private static final String SCREEN_NAME_LINK_FORMAT = "https://vk.com/%s";

    private VkFeedFilter feedFilter = new VkFeedFilter();

    @Override
    public SyndFeed generate(VkFeed feed) {
        Objects.requireNonNull(feed);

        feed = feedFilter.removeDuplicates(feed);

        SyndFeed rss = skeleton();

        List<SyndEntry> entries = new ArrayList<>(feed.items.size());

        for (VkFeedItem item : feed.items) {
            entries.add(generateEntry(item, feed.profiles, feed.groups));
        }

        rss.setEntries(entries);

        return rss;
    }

    @Override
    protected SyndFeed skeleton() {
        SyndFeed rss = new SyndFeedImpl();

        rss.setTitle("VK");
        rss.setPublishedDate(new Date());
        rss.setDescription("VK news feed");
        rss.setLink("http://www.vk.com");
        rss.setFeedType("rss_2.0");

        return rss;
    }

    private SyndEntry generateEntry(VkFeedItem item, Map<Long, VkUser> profiles, Map<Long, VkGroup> groups) {
        assert item != null;
        assert profiles != null;
        assert groups != null;

        SyndEntry entry = new SyndEntryImpl();

        entry.setLink(extractLink(item));
        entry.setPublishedDate(item.date);
        entry.setAuthor(extractAuthor(item, profiles, groups));
        entry.setTitle(entry.getAuthor());

        SyndContent description = new SyndContentImpl();
        description.setType("text/html");
        description.setValue(generateDescription(item, profiles, groups));

        entry.setDescription(description);

        return entry;
    }

    private String extractLink(VkFeedItem item) {
        assert item != null;

        switch (item.type) {
            case POST:
                if(item instanceof VkRepost) {
                    VkRepost repost = (VkRepost) item;
                    return String.format(POST_LINK_FORMAT, repost.sourceID, repost.postID);
                } else {
                    VkPost post = (VkPost) item;
                    return String.format(POST_LINK_FORMAT, post.sourceID, post.postID);
                }
            case PHOTO:
            case WALL_PHOTO:
                VkFeedPhoto photo = (VkFeedPhoto) item;
                return String.format(POST_LINK_FORMAT, photo.sourceID, photo.postID);
            default:
                return "http://www.vk.com";
        }
    }

    private String extractAuthor(VkFeedItem item, Map<Long, VkUser> profiles, Map<Long, VkGroup> groups) {
        assert item != null;
        assert profiles != null;
        assert groups != null;

        return (item.sourceID > 0)
                ? profiles.get(item.sourceID).fullName
                : groups.get(Math.abs(item.sourceID)).name;
    }

    private String generateDescription(VkFeedItem item, Map<Long, VkUser> profiles, Map<Long, VkGroup> groups) {
        assert item != null;
        assert profiles != null;
        assert groups != null;

        switch (item.type) {
            case POST:
                if (item instanceof VkRepost) {
                    return processRepost((VkRepost) item, profiles, groups);
                } else {
                    return processPost((VkPost) item);
                }
            case PHOTO_TAG:
                return processPhotoTags((VkFeedPhotoTag) item, profiles, groups);
            case PHOTO:
            case WALL_PHOTO:
                return processPhotos((VkFeedPhoto) item, profiles, groups);
            default:
                return processUnknownFeedItem(item);
        }
    }

    private String processUnknownFeedItem(VkFeedItem item) {
        assert item != null;
        return "";
    }

    private String processRepost(VkRepost repost, Map<Long, VkUser> profiles, Map<Long, VkGroup> groups) {
        assert repost != null;
        assert profiles != null;
        assert groups != null;

        StringBuilder content = new StringBuilder();

        long sourceID = repost.originalPost.sourceID;
        String author = sourceID > 0 ? profiles.get(sourceID).fullName : groups.get(Math.abs(sourceID)).name;
        String linkToAuthor = linkToSource(sourceID, profiles, groups);
        content.append(String.format("&#8618; <a href='%s'>%s</a>", linkToAuthor, author));

        if(StringUtils.isNotEmpty(repost.text)) {
            content.append("<br/><br/>");
            content.append(repost.text);
        }

        String postContent = processPost(repost.originalPost);

        if(StringUtils.isNotEmpty(postContent)) {
            content.append("<br/><br/>");
            content.append(postContent);
        }

        return content.toString();
    }

    private String linkToSource(long sourceID, Map<Long, VkUser> profiles, Map<Long, VkGroup> groups) {
        assert profiles != null;
        assert groups != null;

        if(sourceID > 0) { // user
            VkUser user = profiles.get(sourceID);

            return StringUtils.isNotEmpty(user.screenName)
                    ? String.format(SCREEN_NAME_LINK_FORMAT, user.screenName)
                    : String.format(USER_ID_LINK_FORMAT, user.id);
        } else { // group
            VkGroup group = groups.get(Math.abs(sourceID));

            return StringUtils.isNotEmpty(group.screenName)
                    ? String.format(SCREEN_NAME_LINK_FORMAT, group.screenName)
                    : String.format(CLUB_LINK_FORMAT, Math.abs(group.id));
        }
    }

    private String processPhotos(VkFeedPhoto photos, Map<Long, VkUser> profiles, Map<Long, VkGroup> groups) {
        assert photos != null;
        assert profiles != null;
        assert groups != null;

        return processPhotos(photos.photos);
    }

    private String processPhotoTags(VkFeedPhotoTag photoTags, Map<Long, VkUser> profiles, Map<Long, VkGroup> groups) {
        assert photoTags != null;
        assert profiles != null;
        assert groups != null;

        return processPhotos(photoTags.photoTags);
    }

    private String processPhotos(List<VkPhoto> photos) {
        assert photos != null;

        StringBuilder content = new StringBuilder();

        for(VkPhoto photo : photos) {
            if(content.length() > 0) {
                content.append("<br/><br/>");
            }

            String link = String.format(PHOTO_LINK_FORMAT, photo.ownerID, photo.id);

            content.append(String.format(
                    "<a href='%s' style='text-decoration: none'>",
                    link
            ));
            content.append(String.format(
                    "<img src='%s'/>",
                    photo.url
            ));
            content.append("</a>");

            if(StringUtils.isNotEmpty(photo.text)) {
                content.append("<br/><br/>");
                content.append(photo.text);
            }
        }

        return content.toString();
    }

    private String processPost(VkPost post) {
        assert post != null;

        StringBuilder content = new StringBuilder();

        if(StringUtils.isNotEmpty(post.text)) {
            content.append(StringUtils.replace(post.text, "\n", "<br/>"));
        }

        String photosContent = processPhotos(post.attachments.photos);

        if(StringUtils.isNotEmpty(photosContent)) {
            if(content.length() > 0) {
                content.append("<br/><br/>");
            }

            content.append(photosContent);
        }

        String postedPhotosContent = processPostedPhotos(post.attachments.postedPhotos);

        if(StringUtils.isNotEmpty(postedPhotosContent)) {
            if(content.length() > 0) {
                content.append("<br/><br/>");
            }

            content.append(postedPhotosContent);
        }

        String audiosContent = processAudios(post.attachments.audios);

        if(StringUtils.isNotEmpty(audiosContent)) {
            if(content.length() > 0) {
                content.append("<br/><br/>");
            }

            content.append(audiosContent);
        }

        String documentsContent = processDocuments(post.attachments.documents);

        if(StringUtils.isNotEmpty(documentsContent)) {
            if(content.length() > 0) {
                content.append("<br/><br/>");
            }

            content.append(documentsContent);
        }

        String videosContent = processVideos(post.attachments.videos);

        if(StringUtils.isNotEmpty(videosContent)) {
            if(content.length() > 0) {
                content.append("<br/><br/>");
            }

            content.append(videosContent);
        }

        String linksContent = processLinks(post.attachments.links);

        if(StringUtils.isNotEmpty(linksContent)) {
            if(content.length() > 0) {
                content.append("<br/><br/>");
            }

            content.append(linksContent);
        }

        String notesContent = processNotes(post.attachments.notes);

        if(StringUtils.isNotEmpty(notesContent)) {
            if(content.length() > 0) {
                content.append("<br/><br/>");
            }

            content.append(notesContent);
        }

        String pagesContent = processPages(post.attachments.pages);

        if(StringUtils.isNotEmpty(pagesContent)) {
            if(content.length() > 0) {
                content.append("<br/><br/>");
            }

            content.append(pagesContent);
        }

        return content.toString();
    }

    private String processPostedPhotos(List<VkPostedPhoto> postedPhotos) {
        assert postedPhotos != null;

        StringBuilder content = new StringBuilder();

        for(VkPostedPhoto photo : postedPhotos) {
            if(content.length() > 0) {
                content.append("<br/><br/>");
            }

            String link = String.format(PHOTO_LINK_FORMAT, photo.ownerID, photo.id);

            content.append(String.format(
                    "<a href='%s' style='text-decoration: none'>",
                    link
            ));
            content.append(String.format(
                    "<img src='%s'/>",
                    photo.url
            ));
            content.append("</a>");
        }

        return content.toString();
    }

    private String processVideos(List<VkVideo> videos) {
        assert videos != null;

        StringBuilder content = new StringBuilder();

        for(VkVideo video : videos) {
            if(content.length() > 0) {
                content.append("<br/><br/>");
            }

            String link = String.format(VIDEO_LINK_FORMAT, video.ownerID, video.id);

            content.append(String.format(
                    "<img src='%s'/>",
                    video.thumbnailURL
            ));
            content.append("<br/>");
            content.append(String.format(
                    "<a href='%s' style='text-decoration: none'>",
                    link
            ));
            content.append(String.format(
                    "&#9658; %s (%d:%d)",
                    video.title,
                    video.duration / 60,
                    video.duration % 60
            ));
            content.append("</a>");

            if(StringUtils.isNotEmpty(video.description)) {
                content.append("<br/><br/>");
                content.append(video.description);
            }
        }

        return content.toString();
    }

    private String processAudios(List<VkAudio> audios) {
        assert audios != null;

        StringBuilder content = new StringBuilder();

        for(VkAudio audio : audios) {
            if(content.length() > 0) {
                content.append("<br/><br/>");
            }

            content.append(String.format(
                    "<a href='%s' style='text-decoration: none'>",
                    audio.url
            ));
            content.append(String.format(
                    "&#9836; %s (%d:%d)",
                    audio.title,
                    audio.duration / 60,
                    audio.duration % 60
            ));
            content.append("</a>");
        }

        return content.toString();
    }

    private String processDocuments(List<VkDocument> documents) {
        assert documents != null;

        StringBuilder content = new StringBuilder();

        for(VkDocument document : documents) {
            if(content.length() > 0) {
                content.append("<br/>");
            }

            content.append(String.format(
                    "<a href='%s' style='text-decoration: none'>&#9632; %s<b>.%s</b></a>",
                    document.url,
                    document.title,
                    document.ext
            ));
        }

        return content.toString();
    }

    private String processLinks(List<VkLink> links) {
        assert links != null;

        StringBuilder content = new StringBuilder();

        for(VkLink link : links) {
            if(content.length() > 0) {
                content.append("<br/>");
            }

            content.append(String.format(
                    "<a href='%s' style='text-decoration: none'>",
                    link.url
            ));
            content.append("&#8627 ");
            content.append(link.title);
            content.append("</a>");

            if(StringUtils.isNotEmpty(link.description)) {
                content.append("<br/>");
                content.append(link.description);
            }
        }

        return content.toString();
    }

    private String processNotes(List<VkNote> notes) {
        assert notes != null;

        StringBuilder content = new StringBuilder();

        for(VkNote note : notes) {
            if(content.length() > 0) {
                content.append("<br/>");
            }

            content.append(String.format(
                    "<a href='%s' style='text-decoration: none'>&#9776; %s</a>",
                    note.url,
                    note.title
            ));
        }

        return content.toString();
    }

    private String processPages(List<VkPage> pages) {
        assert pages != null;

        StringBuilder content = new StringBuilder();

        for(VkPage page : pages) {
            if(content.length() > 0) {
                content.append("<br/>");
            }

            content.append(String.format(
                    "<a href='%s' style='text-decoration: none'>&#9776; %s</a>",
                    page.url,
                    page.title
            ));
        }

        return content.toString();
    }
}
