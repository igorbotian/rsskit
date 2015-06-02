package com.rhcloud.igorbotian.rsskit.rest.facebook;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class FacebookVideo extends FacebookPost {

    public final String name;
    public final String description;
    public final String picture;
    public final String source;
    public final String embedHTML;

    public FacebookVideo(String id, Date createdTime, FacebookProfile from, String caption, String message,
                         String name, String description, String picture, String source, String embedHTML) {

        super(id, createdTime, from, caption, message, FacebookPostType.VIDEO);

        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.picture = Objects.requireNonNull(picture);
        this.source = Objects.requireNonNull(source);
        this.embedHTML = Objects.requireNonNull(embedHTML);
    }

    public static FacebookVideo parse(JsonNode json, String id, Date createdDate, FacebookProfile from,
                                      String caption, String message) {
        Objects.requireNonNull(json);
        Objects.requireNonNull(id);
        Objects.requireNonNull(createdDate);
        Objects.requireNonNull(from);
        Objects.requireNonNull(caption);
        Objects.requireNonNull(message);

        String name = json.has("name") ? json.get("name").asText() : "";
        String description = json.has("description") ? json.get("description").asText() : "";
        String picture = json.has("picture") ? json.get("picture").asText() : "";
        String source = json.has("source") ? json.get("source").asText() : "";
        String embedHTML = json.has("embed_html") ? json.get("embed_html").asText() : "";

        return new FacebookVideo(id, createdDate, from, caption, message, name, description, picture, source, embedHTML);
    }
}
