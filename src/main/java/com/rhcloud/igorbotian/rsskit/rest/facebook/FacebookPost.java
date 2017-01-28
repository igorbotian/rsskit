package com.rhcloud.igorbotian.rsskit.rest.facebook;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian
 */
public abstract class FacebookPost {

    private static final EntityParser<FacebookPost> PARSER = new FacebookPostParser();

    public final String id;
    public final Date createdTime;
    public final FacebookProfile from;
    public final String caption;
    public final String message;
    public final FacebookPostType type;
    public final FacebookPost source;

    public FacebookPost(String id, Date createdTime, FacebookProfile from, String caption,
                        String message, FacebookPostType type, FacebookPost source) {

        this.id = Objects.requireNonNull(id);
        this.createdTime = Objects.requireNonNull(createdTime);
        this.from = Objects.requireNonNull(from);
        this.caption = Objects.requireNonNull(caption);
        this.message = Objects.requireNonNull(message);
        this.type = Objects.requireNonNull(type);
        this.source = source;
    }

    public boolean isRepost() {
        return source != null;
    }

    public abstract FacebookPost asRepostOf(FacebookPost source);

    public static FacebookPost parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class FacebookPostParser extends EntityParser<FacebookPost> {

        @Override
        public FacebookPost parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            String id = getAttribute(json, "id").asText();
            Date createdTime = new Date(getAttribute(json, "created_time").asLong() * 1000);
            FacebookProfile from = FacebookProfile.parse(getAttribute(json, "from"));
            String caption = json.has("caption") ? json.get("caption").asText() : "";
            String message = json.has("message") ? json.get("message").asText() : "";
            FacebookPostType type = identifyType(json);

            switch (type) {
                case LINK:
                    return FacebookLink.parse(json, id, createdTime, from, caption, message);
                case OFFER:
                    return new FacebookOffer(id, createdTime, from, caption, message, null);
                case PHOTO:
                    return FacebookPhoto.parse(json, id, createdTime, from, caption, message);
                case STATUS:
                    return new FacebookStatus(id, createdTime, from, caption, message, null);
                case VIDEO:
                    return FacebookVideo.parse(json, id, createdTime, from, caption, message);
                default:
                    throw new RestParseException("Unexpected Facebook post type: " + type);
            }
        }

        private FacebookPostType identifyType(JsonNode json) {
            assert json != null;

            if(json.has("type")) {
                return FacebookPostType.parse(json.get("type").asText());
            } else if(json.has("metadata")) {
                JsonNode metadata = json.get("metadata");

                if(metadata.has("type")) {
                    return FacebookPostType.parse(metadata.get("type").asText());
                }
            }

            return FacebookPostType.UNKNOWN;
        }
    }
}
