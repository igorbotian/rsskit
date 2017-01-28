package com.rhcloud.igorbotian.rsskit.rest.facebook;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian
 */
public class FacebookProfile {

    private static final EntityParser<FacebookProfile> PARSER = new FacebookProfileParser();

    public final String id;
    public final String name;
    public final String link;

    public FacebookProfile(String id, String name, String link) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.link = Objects.requireNonNull(link);
    }

    public static FacebookProfile parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class FacebookProfileParser extends EntityParser<FacebookProfile> {

        @Override
        public FacebookProfile parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            String id = getAttribute(json, "id").asText();
            String name = getAttribute(json, "name").asText();
            String link = "https://www.facebook.com/" + id;

            return new FacebookProfile(id, name, link);
        }
    }
}
