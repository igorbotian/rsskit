package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkPhotoTag {

    private static final VkEntityParser<VkPhotoTag> PARSER = new VkPhotoTagParser();

    public final long photoID;
    public final long photoOwnerID;
    public final long photoAlbumID;
    public final String photoURL;

    public VkPhotoTag(long photoID, long photoOwnerID, long photoAlbumID, String photoURL) {
        this.photoID = photoID;
        this.photoOwnerID = photoOwnerID;
        this.photoAlbumID = photoAlbumID;
        this.photoURL = Objects.requireNonNull(photoURL);
    }

    public static VkPhotoTag parse(JsonNode json) throws VkException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class VkPhotoTagParser extends VkEntityParser<VkPhotoTag> {

        @Override
        public VkPhotoTag parse(JsonNode json) throws VkException {
            Objects.requireNonNull(json);

            long id = getAttribute(json, "id").asLong();
            long ownerID = getAttribute(json, "owner_id").asLong();
            long albumID = getAttribute(json, "aid").asLong();
            String photoURL = getAttribute(json, "src_big").asText();

            return new VkPhotoTag(id, ownerID, albumID, photoURL);
        }
    }
}
