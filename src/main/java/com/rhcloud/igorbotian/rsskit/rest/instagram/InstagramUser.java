package com.rhcloud.igorbotian.rsskit.rest.instagram;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class InstagramUser {

    private static final EntityParser<InstagramUser> PARSER = new InstagramUserParser();

    public final String id;
    public final String username;
    public final String fullName;
    public final String profilePicture;

    public InstagramUser(String id, String username, String fullName, String profilePicture) {
        this.id = Objects.requireNonNull(id);
        this.username = Objects.requireNonNull(username);
        this.fullName = Objects.requireNonNull(fullName);
        this.profilePicture = Objects.requireNonNull(profilePicture);
    }

    public static InstagramUser parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class InstagramUserParser extends EntityParser<InstagramUser> {

        @Override
        public InstagramUser parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            String id = getAttribute(json, "id").asText();
            String username = getAttribute(json, "username").asText();
            String fullName = getAttribute(json, "full_name").asText();
            String profilePicture = getAttribute(json, "profile_picture").asText();

            return new InstagramUser(id, username, fullName, profilePicture);
        }
    }
}
