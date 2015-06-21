package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkDocument {

    private static final EntityParser<VkDocument> PARSER = new VkDocumentParser();

    public final long id;
    public final long ownerID;
    public final String title;
    public final long size; // bytes
    public final String ext;
    public final String url;
    public final String thumbnailURL;

    public VkDocument(long id, long ownerID, String title, long size, String ext, String url, String thumbnailURL) {
        if(size < 0) {
            throw new IllegalArgumentException("Size should have a positive value");
        }

        this.id = id;
        this.ownerID = ownerID;
        this.title = Objects.requireNonNull(title);
        this.size = size;
        this.ext = Objects.requireNonNull(ext);
        this.url = Objects.requireNonNull(url);
        this.thumbnailURL = Objects.requireNonNull(thumbnailURL);
    }

    public static VkDocument parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class VkDocumentParser extends EntityParser<VkDocument> {

        @Override
        public VkDocument parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            long id = getAttribute(json, "id").asLong();
            long ownerID = getAttribute(json, "owner_id").asLong();
            String title = getAttribute(json, "title").asText();
            int size = getAttribute(json, "size").asInt();
            String ext = getAttribute(json, "ext").asText();
            String url = getAttribute(json, "url").asText();
            String thumbnailURL = json.has("photo_100") ? json.get("photo_100").asText() : "";

            return new VkDocument(id, ownerID, title, size, ext, url, thumbnailURL);
        }
    }
}
