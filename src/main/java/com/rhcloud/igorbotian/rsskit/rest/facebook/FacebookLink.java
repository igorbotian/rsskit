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
public class FacebookLink implements FacebookObject {

    private static final FacebookEntityParser<FacebookLink> PARSER = new FacebookLinkParser();

    public final String id;
    public final Date createdTime;
    public final String description;
    public final FacebookAuthor from;
    public final String link;
    public final String message;
    public final String name;
    public final String caption;
    public final String picture;

    public static FacebookLink parse(JsonNode json, FacebookAPI api, String accessToken) throws RestParseException {
        Objects.requireNonNull(json);
        Objects.requireNonNull(api);
        Objects.requireNonNull(accessToken);

        return PARSER.parse(json, api, accessToken);
    }

    public FacebookLink(String id, Date createdTime, String description, FacebookAuthor from, String link,
                        String message, String name, String caption, String picture) {

        this.id = Objects.requireNonNull(id);
        this.createdTime = Objects.requireNonNull(createdTime);
        this.description = Objects.requireNonNull(description);
        this.from = Objects.requireNonNull(from);
        this.link = Objects.requireNonNull(link);
        this.message = Objects.requireNonNull(message);
        this.name = Objects.requireNonNull(name);
        this.caption = Objects.requireNonNull(caption);
        this.picture = Objects.requireNonNull(picture);
    }

    private static class FacebookLinkParser extends FacebookEntityParser<FacebookLink> {

        private static final Logger LOGGER = LogManager.getLogger(FacebookLinkParser.class);

        @Override
        public FacebookLink parse(JsonNode json, FacebookAPI api, String accessToken) throws RestParseException {
            Objects.requireNonNull(json);
            Objects.requireNonNull(api);
            Objects.requireNonNull(accessToken);

            String id = getAttribute(json, "id").asText();
            FacebookAuthor from = FacebookAuthor.parse(getAttribute(json, "from"), api, accessToken);
            String link = getAttribute(json, "link").asText();
            String description = json.has("description") ? json.get("description").asText() : "";
            String message = json.has("message") ? json.get("message").asText() : "";
            String name = json.has("name") ? json.get("name").asText() : "";
            String caption = json.has("caption") ? json.get("caption").asText() : "";
            String picture = json.has("picture") ? json.get("picture").asText() : "";

            Date createdTime;

            try {
                createdTime = DATE_FORMAT.parse(getAttribute(json, "created_time").asText());
            } catch (ParseException e) {
                LOGGER.error("Unable to parse created_time attribute: " + getAttribute(json, "created_time"));
                createdTime = new Date();
            }

            return new FacebookLink(id, createdTime, description, from, link, message, name, caption, picture);
        }
    }
}
