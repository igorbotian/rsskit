package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkRepost extends VkFeedItem {

    private static final VkEntityParser<VkRepost> PARSER = new VkRepostParser();

    public final long postID;
    public final String text;
    public final VkPost originalPost;

    public VkRepost(VkFeedItem item, long postID, String text, VkPost originalPost) {
        super(item);

        this.postID = postID;
        this.text = Objects.requireNonNull(text);
        this.originalPost = Objects.requireNonNull(originalPost);
    }

    public static VkRepost parse(JsonNode json) throws VkException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    public static boolean isRepost(JsonNode json) throws VkException {
        Objects.requireNonNull(json);
        return json.has("copy_history");
    }

    private static class VkRepostParser extends VkEntityParser<VkRepost> {

        @Override
        public VkRepost parse(JsonNode json) throws VkException {
            Objects.requireNonNull(json);

            VkFeedItem item = VkFeedItem.parse(json);

            long postID = getAttribute(json, "post_id").asLong();
            String text = getAttribute(json, "text").asText();

            JsonNode copyHistory = getAttribute(json, "copy_history");
            VkPost originalPost = VkPost.parse(copyHistory.get(0));

            return new VkRepost(item, postID, text, originalPost);
        }
    }
}
