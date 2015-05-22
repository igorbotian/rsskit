package com.rhcloud.igorbotian.rsskit.rest.facebook.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookProfile;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookVideo;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class FacebookVideoParser extends FacebookPostParser<FacebookVideo> {

    @Override
    protected FacebookVideo parse(JsonNode json, String id, Date createdTime, FacebookProfile from,
                                  String caption, String message) throws RestParseException {

        Objects.requireNonNull(id);
        Objects.requireNonNull(createdTime);
        Objects.requireNonNull(from);
        Objects.requireNonNull(caption);
        Objects.requireNonNull(message);

        String name = json.has("name") ? json.get("name").asText() : "";
        String description = json.has("description") ? json.get("description").asText() : "";
        String picture = json.has("picture") ? json.get("picture").asText() : "";
        String source = json.has("source") ? json.get("source").asText() : "";
        String embedHTML = json.has("embed_html") ? json.get("embed_html").asText() : "";

        return new FacebookVideo(id, createdTime, from, caption, message,
                name, description, picture, source, embedHTML);
    }
}
