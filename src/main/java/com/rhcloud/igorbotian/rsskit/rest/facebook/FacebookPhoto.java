package com.rhcloud.igorbotian.rsskit.rest.facebook;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian
 */
public class FacebookPhoto extends FacebookPost {

    public final String name;
    public final String link;
    public final String image;
    public final String picture;

    public FacebookPhoto(String id, Date createdTime, FacebookProfile from, String caption,
                         String message, String name, String link, String image, String picture,
                         FacebookPost source) {

        super(id, createdTime, from, caption, message, FacebookPostType.PHOTO, source);

        this.name = Objects.requireNonNull(name);
        this.link = Objects.requireNonNull(link);
        this.image = Objects.requireNonNull(image);
        this.picture = Objects.requireNonNull(picture);
    }

    @Override
    public FacebookPhoto asRepostOf(FacebookPost source) {
        Objects.requireNonNull(source);
        return new FacebookPhoto(id, createdTime, from, caption, message, name, link, image, picture, source);
    }

    public static FacebookPhoto parse(JsonNode json, String id, Date createdDate, FacebookProfile from,
                                     String caption, String message) {
        Objects.requireNonNull(json);
        Objects.requireNonNull(id);
        Objects.requireNonNull(createdDate);
        Objects.requireNonNull(from);
        Objects.requireNonNull(caption);
        Objects.requireNonNull(message);

        String name = json.has("name") ? json.get("name").asText() : "";
        String link = json.has("link") ? json.get("link").asText() : "";
        String image = json.has("image") ? json.get("image").asText() : "";
        String picture = json.has("picture") ? json.get("picture").asText() : "";

        return new FacebookPhoto(id, createdDate, from, caption, message, name, link, image, picture, null);
    }
}
