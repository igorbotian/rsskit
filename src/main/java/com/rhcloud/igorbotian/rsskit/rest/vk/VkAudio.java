package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkAudio {

    private static final VkAudioParser PARSER = new VkAudioParser();

    public final long id;
    public final long ownerID;
    public final String artist;
    public final String title;
    public final int duration; // seconds
    public final String url;

    public VkAudio(long id, long ownerID, String artist, String title, int duration, String url) {
        if (duration < 0) {
            throw new IllegalArgumentException("Duration should have a positive value");
        }

        this.id = id;
        this.ownerID = ownerID;
        this.artist = Objects.requireNonNull(artist);
        this.title = Objects.requireNonNull(title);
        this.duration = duration;
        this.url = Objects.requireNonNull(url);
    }

    public static VkAudio parse(JsonNode json) throws VkException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class VkAudioParser extends VkEntityParser<VkAudio> {

        @Override
        public VkAudio parse(JsonNode json) throws VkException {
            Objects.requireNonNull(json);

            long id = getAttribute(json, "id").asLong();
            long ownerID = getAttribute(json, "owner_id").asLong();
            String artist = getAttribute(json, "artist").asText();
            String title = getAttribute(json, "title").asText();
            int duration = getAttribute(json, "duration").asInt();
            String url = getAttribute(json, "url").asText();

            return new VkAudio(id, ownerID, artist, title, duration, url);
        }
    }
}
