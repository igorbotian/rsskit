package com.rhcloud.igorbotian.rsskit.rest.facebook.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookProfile;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookLink;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class FacebookLinkParser extends FacebookPostParser<FacebookLink> {

    @Override
    protected FacebookLink parse(JsonNode json, String id, Date createdTime, FacebookProfile from,
                                 String caption, String message)
            throws RestParseException {

        Objects.requireNonNull(json);
        Objects.requireNonNull(id);
        Objects.requireNonNull(createdTime);
        Objects.requireNonNull(from);
        Objects.requireNonNull(caption);
        Objects.requireNonNull(message);

        String link = getAttribute(json, "link").asText();
        String description = json.has("description") ? json.get("description").asText() : "";
        String name = json.has("name") ? json.get("name").asText() : "";
        String picture = json.has("picture") ? json.get("picture").asText() : "";

        return new FacebookLink(id, createdTime, from, caption, message, description, link, name, picture);
    }
}
