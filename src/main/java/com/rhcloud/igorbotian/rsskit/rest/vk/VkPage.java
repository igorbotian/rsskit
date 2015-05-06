package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkPage {

    private static final EntityParser<VkPage> PARSER = new VkPageParser();

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

    public static VkPage parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class VkPageParser extends EntityParser<VkPage> {

        @Override
        public VkPage parse(JsonNode json) throws RestParseException {
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
