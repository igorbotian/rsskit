package com.rhcloud.igorbotian.rsskit.rss.vk;

import com.rhcloud.igorbotian.rsskit.rest.vk.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class VkFeedFilter {

    public VkFeed removeDuplicates(VkFeed feed) {
        Objects.requireNonNull(feed);
        return new VkFeed(removeDuplicates(feed.items), feed.profiles, feed.groups);
    }

    private List<VkFeedItem> removeDuplicates(List<VkFeedItem> items) {
        assert items != null;

        LinkedHashMap<Identifier, VkFeedItem> uniqueItems = new LinkedHashMap<>();

        for(VkFeedItem item : items) {
            uniqueItems.put(new Identifier(item.date, item.sourceID), item);
        }

        List<VkFeedItem> result = new ArrayList<>(uniqueItems.size());

        for(Map.Entry<Identifier, VkFeedItem> item : uniqueItems.entrySet()) {
            result.add(item.getValue());
        }

        return remoteDuplicatedPhotosFromPosts(result);
    }

    private List<VkFeedItem> remoteDuplicatedPhotosFromPosts(List<VkFeedItem> items) {
        List<VkFeedItem> result = new ArrayList<>(items.size());

        for(VkFeedItem item : items) {
            if(item instanceof VkFeedPhoto) {
                if(isPhotoFromPost((VkFeedPhoto) item, items)) {
                    continue;
                }
            }

            result.add(item);
        }

        return result;
    }

    private boolean isPhotoFromPost(VkFeedPhoto photo, List<VkFeedItem> items) {
        for(VkPhoto item : photo.photos) {
            if(isAttachedToAnyPost(item, items)) {
                return true;
            }
        }

        return false;
    }

    private boolean isAttachedToAnyPost(VkPhoto photo, List<VkFeedItem> items) {
        for(VkFeedItem item : items) {
            if(item instanceof VkPost) {
                if(isAttachedToPost(photo, (VkPost) item)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isAttachedToPost(VkPhoto photo, VkPost post) {
        for(VkPhoto postPhoto : post.attachments.photos) {
            if(StringUtils.equals(photo.url, postPhoto.url)) {
                return true;
            }
        }

        return false;
    }

    private static class Identifier {

        public final Date date;
        public final long sourceID;

        private Identifier(Date date, long sourceID) {
            this.date = Objects.requireNonNull(date);
            this.sourceID = Objects.requireNonNull(sourceID);
        }

        @Override
        public int hashCode() {
            return Objects.hash(date, sourceID);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj == null || !(obj instanceof Identifier)) {
                return false;
            }

            Identifier other = (Identifier) obj;
            return Objects.equals(date, other.date)
                    && Objects.equals(sourceID, other.sourceID);
        }
    }
}
