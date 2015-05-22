package com.rhcloud.igorbotian.rsskit.rest.facebook.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookProfile;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookPost;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public abstract class FacebookPostParser<T extends FacebookPost> extends EntityParser<T> {

    private static final FacebookProfileParser FROM_PARSER = new FacebookProfileParser();

    @Override
    public T parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);

        String id = getAttribute(json, "id").asText();
        Date createdTime = new Date(1000 * getAttribute(json, "created_time").asLong());
        FacebookProfile from = FROM_PARSER.parse(getAttribute(json, "from"));
        String caption = json.has("caption") ? json.get("caption").asText() : "";
        String message = json.has("message") ? json.get("message").asText() : "";

        return parse(json, id, createdTime, from, caption, message);
    }

    protected abstract T parse(JsonNode json, String id, Date createdTime, FacebookProfile from,
                               String caption, String message) throws RestParseException;
}
