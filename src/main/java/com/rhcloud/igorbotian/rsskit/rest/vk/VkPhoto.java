package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkPhoto {

    private static final VkParser<VkPhoto> PARSER = new VkPhotoParser();

    public final long id;
    public final long ownerID;
    public final long albumID;
    public final String url;
    public final String text;

    public VkPhoto(long id, long ownerID, long albumID, String url, String text) {
        this.id = id;
        this.ownerID = ownerID;
        this.albumID = albumID;
        this.url = Objects.requireNonNull(url);
        this.text = text != null ? text : "";
    }

    public static VkPhoto parse(JsonNode json) throws VkException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class VkPhotoParser extends VkParser<VkPhoto> {

        @Override
        public VkPhoto parse(JsonNode json) throws VkException {
            Objects.requireNonNull(json);

            long id = getAttribute(json, "id").asLong();
            long albumID = getAttribute(json, "album_id").asLong();
            long ownerID = getAttribute(json, "owner_id").asLong();
            String url = getAttribute(json, "photo_604").asText();
            String text = getAttribute(json, "text").asText();

            return new VkPhoto(id, ownerID, albumID, url, text);
        }
    }
}
