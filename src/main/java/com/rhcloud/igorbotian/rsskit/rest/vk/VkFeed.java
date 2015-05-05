package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkFeed {

    private static final VkParser<VkFeed> PARSER = new VkFeedParser();

    public final List<VkFeedItem> items;
    public final Map<Long, VkUser> profiles;
    public final Map<Long, VkGroup> groups;

    public VkFeed(List<VkFeedItem> items, Map<Long, VkUser> profiles, Map<Long, VkGroup> groups) {
        this.items = Collections.unmodifiableList(Objects.requireNonNull(items));
        this.profiles = Collections.unmodifiableMap(Objects.requireNonNull(profiles));
        this.groups = Collections.unmodifiableMap(Objects.requireNonNull(groups));
    }

    public static VkFeed parse(JsonNode json) throws VkException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class VkFeedParser extends VkParser<VkFeed> {

        @Override
        public VkFeed parse(JsonNode json) throws VkException {
            Objects.requireNonNull(json);

            Map<Long, VkUser> profiles = parseProfiles(getAttribute(json, "profiles"));
            Map<Long, VkGroup> groups = parseGroups(getAttribute(json, "groups"));
            List<VkFeedItem> items = parseItems(getAttribute(json, "items"));

            return new VkFeed(items, profiles, groups);
        }

        private Map<Long, VkUser> parseProfiles(JsonNode json) throws VkException {
            assert json != null;

            Map<Long, VkUser> profiles = new HashMap<>(json.size());

            for(int i = 0; i < json.size(); i++) {
                VkUser profile = VkUser.parse(json.get(i));
                profiles.put(profile.id, profile);
            }

            return profiles;
        }

        private Map<Long, VkGroup> parseGroups(JsonNode json) throws VkException {
            assert json != null;

            Map<Long, VkGroup> groups = new HashMap<>(json.size());

            for(int i = 0; i < json.size(); i++) {
                VkGroup group = VkGroup.parse(json.get(i));
                groups.put(group.id, group);
            }

            return groups;
        }

        private List<VkFeedItem> parseItems(JsonNode json) throws VkException {
            assert json != null;

            List<VkFeedItem> items = new ArrayList<>(json.size());

            for(int i = 0; i < json.size(); i++) {
                items.add(parseItem(json.get(i)));
            }

            return items;
        }

        private VkFeedItem parseItem(JsonNode json) throws VkException {
            assert json != null;

            VkFeedItem item = VkFeedItem.parse(json);

            switch (item.type) {
                case PHOTO:
                case WALL_PHOTO:
                    return VkFeedPhoto.parse(json);
                case PHOTO_TAG:
                    return VkFeedPhotoTag.parse(json);
                case POST:
                    return VkRepost.isRepost(json) ? VkRepost.parse(json) : VkPost.parse(json);
                default:
                    return item;
            }
        }
    }
}
