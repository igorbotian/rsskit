package com.rhcloud.igorbotian.rsskit.rest.facebook.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookPostType;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookProfile;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class IncompleteFacebookPost {

    public static final EntityParser<IncompleteFacebookPost> PARSER = new IncompleteFacebookPostParser();

    public final String id;
    public final Date createdTime;
    public final FacebookProfile from;
    public final FacebookPostType type;
    public final String message;
    public final String caption;
    public final String description;
    public final String link;
    public final String name;
    public final String picture;
    public final String videoSource;
    public final String objectID;
    public IncompleteFacebookPost source;

    IncompleteFacebookPost(String id, Date createdTime, FacebookProfile from, FacebookPostType type,
                           String message, String caption, String description, String link, String name,
                           String picture, String videoSource, String objectID) {

        this.id = Objects.requireNonNull(id);
        this.createdTime = Objects.requireNonNull(createdTime);
        this.from = Objects.requireNonNull(from);
        this.type = Objects.requireNonNull(type);
        this.message = Objects.requireNonNull(message);
        this.caption = Objects.requireNonNull(caption);
        this.description = Objects.requireNonNull(description);
        this.link = Objects.requireNonNull(link);
        this.name = Objects.requireNonNull(name);
        this.picture = Objects.requireNonNull(picture);
        this.videoSource = Objects.requireNonNull(videoSource);
        this.objectID = objectID;
    }

    public boolean isRepost() {
        return source != null;
    }

    private static class IncompleteFacebookPostParser extends EntityParser<IncompleteFacebookPost> {

        @Override
        public IncompleteFacebookPost parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            String id = getAttribute(json, "id").asText();
            Date createdTime = new Date(1000 * getAttribute(json, "created_time").asLong());
            FacebookProfile from = FacebookProfile.parse(getAttribute(json, "from"));
            FacebookPostType type = FacebookPostType.parse(getAttribute(json, "type").asText());
            String message = json.has("message") ? json.get("message").asText() : "";
            String caption = json.has("caption") ? json.get("caption").asText() : "";
            String description = json.has("description") ? json.get("description").asText() : "";
            String link = json.has("link") ? json.get("link").asText() : "";
            String name = json.has("name") ? json.get("name").asText() : "";
            String picture = json.has("picture") ? json.get("picture").asText() : "";
            String videoSource = json.has("source") ? json.get("source").asText() : "";
            String objectID = json.has("object_id") ? json.get("object_id").asText() : null;

            return new IncompleteFacebookPost(id, createdTime, from, type, message, caption, description,
                    link, name, picture, videoSource, objectID);
        }
    }
}
