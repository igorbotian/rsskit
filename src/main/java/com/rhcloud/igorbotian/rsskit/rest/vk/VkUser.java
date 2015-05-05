package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkUser {

    private static final VkParser<VkUser> PARSER = new VkUserParser();

    public final long id;
    public final String fullName;
    public final String photoURL;

    public VkUser(long id, String fullName, String photoURL) {
        this.id = id;
        this.fullName = Objects.requireNonNull(fullName);
        this.photoURL = Objects.requireNonNull(photoURL);
    }

    public static VkUser parse(JsonNode json) throws VkException {
        return PARSER.parse(json);
    }

    private static class VkUserParser extends VkParser<VkUser> {

        @Override
        public VkUser parse(JsonNode json) throws VkException {
            Objects.requireNonNull(json);

            long id = getAttribute(json, "id").asLong();
            String firstName = getAttribute(json, "first_name").asText();
            String lastName = getAttribute(json, "last_name").asText();
            String photoURL = getAttribute(json, "photo_50").asText();

            return new VkUser(id, firstName + " " + lastName, photoURL);
        }
    }
}
