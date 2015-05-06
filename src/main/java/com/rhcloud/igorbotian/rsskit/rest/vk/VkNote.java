package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkNote {

    private static final EntityParser<VkNote> PARSER = new VkNoteParser();

    public final long id;
    public final long ownerID;
    public final String title;
    public final String text;
    public final String url;

    public VkNote(long id, long ownerID, String title, String text, String url) {
        this.id = id;
        this.ownerID = ownerID;
        this.title = Objects.requireNonNull(title);
        this.text = Objects.requireNonNull(text);
        this.url = Objects.requireNonNull(url);
    }

    public static VkNote parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class VkNoteParser extends EntityParser<VkNote> {

        @Override
        public VkNote parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            long id = getAttribute(json, "id").asLong();
            long ownerID = getAttribute(json, "user_id").asLong();
            String title = getAttribute(json, "title").asText();
            String text = getAttribute(json, "text").asText();
            String url = getAttribute(json, "view_url").asText();

            return new VkNote(id, ownerID, title, text, url);
        }
    }
}
