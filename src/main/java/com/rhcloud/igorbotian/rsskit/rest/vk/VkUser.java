package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkUser {

    private static final EntityParser<VkUser> PARSER = new VkUserParser();

    public final long id;
    public final String fullName;
    public final String screenName;
    public final String photoURL;

    public VkUser(long id, String fullName, String screenName, String photoURL) {
        this.id = id;
        this.fullName = Objects.requireNonNull(fullName);
        this.screenName = screenName;
        this.photoURL = Objects.requireNonNull(photoURL);
    }

    public static VkUser parse(JsonNode json) throws RestParseException {
        return PARSER.parse(json);
    }

    private static class VkUserParser extends EntityParser<VkUser> {

        @Override
        public VkUser parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            long id = getAttribute(json, "id").asLong();
            String firstName = getAttribute(json, "first_name").asText();
            String lastName = getAttribute(json, "last_name").asText();
            String screenName = json.has("screen_name") ? json.get("screen_name").asText() : null;
            String photoURL = getAttribute(json, "photo_50").asText();

            return new VkUser(id, firstName + " " + lastName, screenName, photoURL);
        }
    }
}
