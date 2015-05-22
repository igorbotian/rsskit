package com.rhcloud.igorbotian.rsskit.rest.facebook.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookProfile;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookPhoto;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class FacebookPhotoParser extends FacebookPostParser<FacebookPhoto> {

    @Override
    protected FacebookPhoto parse(JsonNode json, String id, Date createdTime, FacebookProfile from,
                                  String caption, String message) throws RestParseException {

        Objects.requireNonNull(json);
        Objects.requireNonNull(id);
        Objects.requireNonNull(createdTime);
        Objects.requireNonNull(from);
        Objects.requireNonNull(caption);
        Objects.requireNonNull(message);

        String name = json.has("name") ? json.get("name").asText() : "";
        String link = json.has("link") ? json.get("link").asText() : "";
        String picture = json.has("picture") ? json.get("picture").asText() : "";

        return new FacebookPhoto(id, createdTime, from, caption, message, name, link, picture);
    }
}
