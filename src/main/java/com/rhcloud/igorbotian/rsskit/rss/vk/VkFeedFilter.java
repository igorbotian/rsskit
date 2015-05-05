package com.rhcloud.igorbotian.rsskit.rss.vk;

import com.rhcloud.igorbotian.rsskit.rest.vk.VkFeed;
import com.rhcloud.igorbotian.rsskit.rest.vk.VkFeedItem;

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

        LinkedHashMap<Identifier, VkFeedItem> result = new LinkedHashMap<>();

        for(VkFeedItem item : items) {
            result.put(new Identifier(item.date, item.sourceID), item);
        }

        return new ArrayList<>(result.values());
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
