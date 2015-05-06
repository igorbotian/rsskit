package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkGroup {

    private static final VkEntityParser<VkGroup> PARSER = new VkGroupParser();

    public final long id;
    public final String name;
    public final String photoURL;

    public VkGroup(long id, String name, String photoURL) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.photoURL = Objects.requireNonNull(photoURL);
    }

    public static VkGroup parse(JsonNode json) throws VkException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class VkGroupParser extends VkEntityParser<VkGroup> {

        @Override
        public VkGroup parse(JsonNode json) throws VkException {
            Objects.requireNonNull(json);

            long id = getAttribute(json, "id").asLong();
            String name = getAttribute(json, "name").asText();
            String photoURL = getAttribute(json, "photo_50").asText();

            return new VkGroup(id, name, photoURL);
        }
    }
}
