package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkPhotoTag {

    private static final EntityParser<VkPhotoTag> PARSER = new VkPhotoTagParser();

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

    public static VkPhotoTag parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class VkPhotoTagParser extends EntityParser<VkPhotoTag> {

        @Override
        public VkPhotoTag parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            long id = getAttribute(json, "id").asLong();
            long ownerID = getAttribute(json, "owner_id").asLong();
            long albumID = getAttribute(json, "aid").asLong();
            String photoURL = getAttribute(json, "src_big").asText();

            return new VkPhotoTag(id, ownerID, albumID, photoURL);
        }
    }
}
