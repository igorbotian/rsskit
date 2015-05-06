package com.rhcloud.igorbotian.rsskit.rest.twitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterUser {

    private static final EntityParser<TwitterUser> PARSER = new TwitterUserParser();

    public final String name;
    public final String screenName;
    public final String profileImageURL;

    public TwitterUser(String name, String screenName, String profileImageURL) {
        this.name = name;
        this.screenName = screenName;
        this.profileImageURL = profileImageURL;
    }

    public static TwitterUser parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class TwitterUserParser extends EntityParser<TwitterUser> {

        @Override
        public TwitterUser parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            String name = getAttribute(json, "name").asText();
            String screenName = getAttribute(json, "screen_name").asText();
            String profileImageURL = getAttribute(json, "profile_image_url").asText();

            return new TwitterUser(name, screenName, profileImageURL);
        }
    }
}
