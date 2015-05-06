package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkVideo {

    private static final EntityParser<VkVideo> PARSER = new VkVideoParser();

    public final long id;
    public final long ownerID;
    public final String title;
    public final String description;
    public final int duration; // seconds
    public final String thumbnailURL;

    public VkVideo(long id, long ownerID, String title, String description, int duration, String thumbnailURL) {
        if(duration < 0) {
            throw new IllegalArgumentException("Duration should have a positive value");
        }

        this.id = id;
        this.ownerID = ownerID;
        this.title = Objects.requireNonNull(title);
        this.description = Objects.requireNonNull(description);
        this.duration = duration;
        this.thumbnailURL = Objects.requireNonNull(thumbnailURL);
    }

    public static VkVideo parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class VkVideoParser extends EntityParser<VkVideo> {

        @Override
        public VkVideo parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            long id = getAttribute(json, "id").asLong();
            long ownerID = getAttribute(json, "owner_id").asLong();
            String title = getAttribute(json, "title").asText();
            String description = getAttribute(json, "description").asText();
            int duration = getAttribute(json, "duration").asInt();
            String thumbnailURL = getAttribute(json, "photo_320").asText();

            return new VkVideo(id, ownerID, title, description, duration, thumbnailURL);
        }
    }
}
