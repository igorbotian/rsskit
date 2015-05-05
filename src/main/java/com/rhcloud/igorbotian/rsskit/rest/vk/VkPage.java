package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkPage {

    private static final VkParser<VkPage> PARSER = new VkPageParser();

    public final long id;
    public final long groupID;
    public final long authorID;
    public final String title;
    public final String url;

    public VkPage(long id, long groupID, long authorID, String title, String url) {
        this.id = id;
        this.groupID = groupID;
        this.authorID = authorID;
        this.title = Objects.requireNonNull(title);
        this.url = Objects.requireNonNull(url);
    }

    public static VkPage parse(JsonNode json) throws VkException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class VkPageParser extends VkParser<VkPage> {

        @Override
        public VkPage parse(JsonNode json) throws VkException {
            Objects.requireNonNull(json);

            long id = getAttribute(json, "id").asLong();
            long groupID = getAttribute(json, "group_id").asLong();
            long authorID = getAttribute(json, "author_id").asLong();
            String title = getAttribute(json, "title").asText();
            String url = getAttribute(json, "view_url").asText();

            return new VkPage(id, groupID, authorID, title, url);
        }
    }
}
