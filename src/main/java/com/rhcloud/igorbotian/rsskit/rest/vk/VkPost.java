package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkPost extends VkFeedItem {

    private static final VkEntityParser<VkPost> PARSER = new VkPostParser();

    public final long postID;
    public final String text;
    public final VkAttachments attachments;

    public VkPost(VkPost post) {
        super(new VkFeedItem(post.type, post.sourceID, post.date));

        this.postID = post.postID;
        this.text = post.text;
        this.attachments = post.attachments;
    }

    public VkPost(VkFeedItem item, long postID, String text, VkAttachments attachments) {
        super(item.type, item.sourceID, item.date);

        this.postID = postID;
        this.text = Objects.requireNonNull(text);
        this.attachments = Objects.requireNonNull(attachments);
    }

    public static VkPost parse(JsonNode json) throws VkException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class VkPostParser extends VkEntityParser<VkPost> {

        @Override
        public VkPost parse(JsonNode json) throws VkException {
            Objects.requireNonNull(json);

            VkFeedItem item = VkFeedItem.parse(json);
            long postID = json.has("post_id")
                    ? json.get("post_id").asLong()
                    : json.get("id").asLong(); // original post
            String text = getAttribute(json, "text").asText();
            VkAttachments attachments = json.has("attachments")
                    ? VkAttachments.parse(json.get("attachments"))
                    : new VkAttachments();

            return new VkPost(item, postID, text, attachments);
        }
    }
}
