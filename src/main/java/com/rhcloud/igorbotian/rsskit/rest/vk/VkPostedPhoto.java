package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkPostedPhoto {

    private static final VkParser<VkPostedPhoto> PARSER = new VkPostedPhotoParser();

    public final long id;
    public final long ownerID;
    public final String url;

    public VkPostedPhoto(long id, long ownerID, String url) {
        this.id = id;
        this.ownerID = ownerID;
        this.url = Objects.requireNonNull(url);
    }

    public static VkPostedPhoto parse(JsonNode json) throws VkException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class VkPostedPhotoParser extends VkParser<VkPostedPhoto> {

        @Override
        public VkPostedPhoto parse(JsonNode json) throws VkException {
            Objects.requireNonNull(json);

            long id = getAttribute(json, "id").asLong();
            long ownerID = getAttribute(json, "owner_id").asLong();
            String url = getAttribute(json, "photo_604").asText();

            return new VkPostedPhoto(id, ownerID, url);
        }
    }
}
