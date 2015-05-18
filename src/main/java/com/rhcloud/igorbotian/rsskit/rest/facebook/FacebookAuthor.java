package com.rhcloud.igorbotian.rsskit.rest.facebook;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class FacebookAuthor implements FacebookObject {

    private static final FacebookEntityParser<FacebookAuthor> PARSER = new FacebookUserParser();

    public final String id;
    public final String name;
    public final String link;

    public FacebookAuthor(String id, String name, String link) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.link = Objects.requireNonNull(link);
    }

    public static FacebookAuthor parse(JsonNode json, FacebookAPI api, String accessToken) throws RestParseException {
        Objects.requireNonNull(json);
        Objects.requireNonNull(api);
        Objects.requireNonNull(accessToken);

        return PARSER.parse(json, api, accessToken);
    }

    private static class FacebookUserParser extends FacebookEntityParser<FacebookAuthor> {

        @Override
        public FacebookAuthor parse(JsonNode json, FacebookAPI api, String accessToken) throws RestParseException {
            Objects.requireNonNull(json);
            Objects.requireNonNull(api);
            Objects.requireNonNull(accessToken);

            String id = getAttribute(json, "id").asText();
            String name = getAttribute(json, "name").asText();
            String link = "https://www.facebook.com/" + id;

            return new FacebookAuthor(id, name, link);
        }
    }
}
