package com.rhcloud.igorbotian.rsskit.rest.facebook;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class FacebookVideo implements FacebookObject {

    private static final FacebookEntityParser<FacebookVideo> PARSER = new FacebookVideoParser();

    public final String id;
    public final FacebookAuthor from;
    public final Date createdTime;
    public final String message;
    public final String picture;
    public final String source;
    public final String caption;
    public final String name;
    public final String description;

    public FacebookVideo(String id, FacebookAuthor from, Date createdTime, String message, String picture,
                         String source, String caption, String name, String description) {

        this.id = Objects.requireNonNull(id);
        this.from = Objects.requireNonNull(from);
        this.createdTime = Objects.requireNonNull(createdTime);
        this.message = Objects.requireNonNull(message);
        this.picture = Objects.requireNonNull(picture);
        this.source = Objects.requireNonNull(source);
        this.caption = Objects.requireNonNull(caption);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
    }

    public static FacebookVideo parse(JsonNode json, FacebookAPI api, String accessToken) throws RestParseException {
        Objects.requireNonNull(json);
        Objects.requireNonNull(api);
        Objects.requireNonNull(accessToken);

        return PARSER.parse(json, api, accessToken);
    }

    private static class FacebookVideoParser extends FacebookEntityParser<FacebookVideo> {

        private static final Logger LOGGER = LogManager.getLogger(FacebookVideoParser.class);

        @Override
        public FacebookVideo parse(JsonNode json, FacebookAPI api, String accessToken) throws RestParseException {
            Objects.requireNonNull(json);
            Objects.requireNonNull(api);
            Objects.requireNonNull(accessToken);

            String id = getAttribute(json, "id").asText();
            FacebookAuthor from = FacebookAuthor.parse(getAttribute(json, "from"), api, accessToken);
            String description = json.has("description") ? json.get("description").asText() : "";
            String message = json.has("message") ? json.get("message").asText() : "";
            String picture = json.has("picture") ? json.get("picture").asText() : "";
            String source = json.has("source") ? json.get("source").asText() : "";
            String caption = json.has("caption") ? json.get("caption").asText() : "";
            String name = json.has("name") ? json.get("name").asText() : "";

            Date createdTime;

            try {
                createdTime = DATE_FORMAT.parse(getAttribute(json, "created_time").asText());
            } catch (ParseException e) {
                LOGGER.error("Unable to parse created_time attribute: " + getAttribute(json, "created_time"));
                createdTime = new Date();
            }

            return new FacebookVideo(id, from, createdTime, message, picture, source, caption, name, description);
        }
    }
}
