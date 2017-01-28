package com.rhcloud.igorbotian.rsskit.rest.facebook.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookPost;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookPostType;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookProfile;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian
 */
public class IncompleteFacebookNotification {

    private static final EntityParser<IncompleteFacebookNotification> PARSER = new IncompleteFacebookNotificationParser();

    public final String id;
    public final FacebookProfile from;
    public final Date createdTime;
    public final String title;
    public final String link;
    public final boolean unread;
    public final JsonNode object;

    public IncompleteFacebookNotification(String id, FacebookProfile from, Date createdTime, String title,
                                          String link, boolean unread, JsonNode object) {
        this.id = Objects.requireNonNull(id);
        this.from = Objects.requireNonNull(from);
        this.createdTime = Objects.requireNonNull(createdTime);
        this.title = Objects.requireNonNull(title);
        this.link = Objects.requireNonNull(link);
        this.unread = Objects.requireNonNull(unread);
        this.object = Objects.requireNonNull(object);
    }

    public static IncompleteFacebookNotification parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    public boolean isObjectComplete() {
        if(object.has("type")) {
            FacebookPostType type = FacebookPostType.parse(object.get("type").asText());
            return !FacebookPostType.UNKNOWN.equals(type);
        }

        return false;
    }

    public boolean isObjectIdentified() {
        return object.has("id");
    }

    public String getObjectID() {
        if(!isObjectIdentified()) {
            throw new IllegalStateException("Notification object has no ID attribute");
        }

        return object.get("id").asText();
    }

    public FacebookPost parseCompleteObject() throws RestParseException {
        if(!isObjectComplete()) {
            throw new IllegalStateException("Can't parse notification object: it isn't complete");
        }

        return FacebookPost.parse(object);
    }

    private static class IncompleteFacebookNotificationParser extends EntityParser<IncompleteFacebookNotification> {

        @Override
        public IncompleteFacebookNotification parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            String id = getAttribute(json, "id").asText();
            Date createdTime = new Date(getAttribute(json, "created_time").asLong() * 1000);
            String title = getAttribute(json, "title").asText();
            String link = getAttribute(json, "link").asText();
            boolean unread = (1 == getAttribute(json, "unread").asInt());
            FacebookProfile from = FacebookProfile.parse(getAttribute(json, "from"));
            JsonNode object = getAttribute(json, "object");

            return new IncompleteFacebookNotification(id, from, createdTime, title, link, unread, object);
        }
    }
}
