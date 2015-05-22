package com.rhcloud.igorbotian.rsskit.rest.facebook.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookProfile;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class FacebookProfileParser extends EntityParser<FacebookProfile> {

    @Override
    public FacebookProfile parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);

        String id = getAttribute(json, "id").asText();
        String name = getAttribute(json, "name").asText();
        String link = "https://www.facebook.com/" + id;

        return new FacebookProfile(id, name, link);
    }
}
