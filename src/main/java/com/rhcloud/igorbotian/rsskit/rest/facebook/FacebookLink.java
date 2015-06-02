package com.rhcloud.igorbotian.rsskit.rest.facebook;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class FacebookLink extends FacebookPost {

    public final String description;
    public final String link;
    public final String name;
    public final String picture;

    public FacebookLink(String id, Date createdTime, FacebookProfile from, String caption,
                        String message, String description, String link, String name, String picture) {

        super(id, createdTime, from, caption, Objects.equals(message, link) ? "" : message, FacebookPostType.LINK);

        this.description = Objects.requireNonNull(description);
        this.link = Objects.requireNonNull(link);
        this.name = Objects.requireNonNull(name);
        this.picture = Objects.requireNonNull(picture);
    }

    public static FacebookLink parse(JsonNode json, String id, Date createdDate, FacebookProfile from,
                                     String caption, String message) {
        Objects.requireNonNull(json);
        Objects.requireNonNull(id);
        Objects.requireNonNull(createdDate);
        Objects.requireNonNull(from);
        Objects.requireNonNull(caption);
        Objects.requireNonNull(message);

        String description = json.has("description") ? json.get("description").asText() : "";
        String link = json.has("link") ? json.get("link").asText() : "";
        String name = json.has("name") ? json.get("name").asText() : "";
        String picture = json.has("picture") ? json.get("picture").asText() : "";

        return new FacebookLink(id, createdDate, from, caption, message, description, link, name, picture);
    }
}
