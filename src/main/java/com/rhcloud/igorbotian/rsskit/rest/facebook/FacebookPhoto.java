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
public class FacebookPhoto implements FacebookObject {

    private static final FacebookEntityParser<FacebookPhoto> PARSER = new FacebookPhotoParser();

    public final String id;
    public final Date createdTime;
    public final FacebookAuthor from;
    public final String name;
    public final String link;
    public final String source;

    public FacebookPhoto(String id, Date createdTime, FacebookAuthor from, String name, String link, String source) {
        this.id = Objects.requireNonNull(id);
        this.createdTime = Objects.requireNonNull(createdTime);
        this.from = Objects.requireNonNull(from);
        this.name = Objects.requireNonNull(name);
        this.link = Objects.requireNonNull(link);
        this.source = Objects.requireNonNull(source);
    }

    public static FacebookPhoto parse(JsonNode json, FacebookAPI api, String accessToken) throws RestParseException {
        Objects.requireNonNull(json);
        Objects.requireNonNull(api);
        Objects.requireNonNull(accessToken);

        return PARSER.parse(json, api, accessToken);
    }

    private static class FacebookPhotoParser extends FacebookEntityParser<FacebookPhoto> {

        private static final Logger LOGGER = LogManager.getLogger(FacebookPhotoParser.class);

        @Override
        public FacebookPhoto parse(JsonNode json, FacebookAPI api, String accessToken) throws RestParseException {
            Objects.requireNonNull(json);
            Objects.requireNonNull(api);
            Objects.requireNonNull(accessToken);

            String id = getAttribute(json, "id").asText();
            String name = json.has("name") ? json.get("name").asText() : "";
            String link = json.has("link") ? json.get("link").asText() : "";
            String source = json.has("source") ? json.get("source").asText() : "";
            FacebookAuthor from = FacebookAuthor.parse(getAttribute(json, "from"), api, accessToken);

            Date createdTime;

            try {
                createdTime = DATE_FORMAT.parse(getAttribute(json, "created_time").asText());
            } catch (ParseException e) {
                LOGGER.error("Unable to parse created_time attribute: " + getAttribute(json, "created_time"));
                createdTime = new Date();
            }

            return new FacebookPhoto(id, createdTime, from, name, link, source);
        }
    }
}
