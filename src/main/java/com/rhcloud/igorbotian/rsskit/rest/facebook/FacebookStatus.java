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
public class FacebookStatus implements FacebookObject {

    private static final FacebookEntityParser<FacebookStatus> PARSER = new FacebookStatusParser();

    public final String id;
    public final Date updatedTime;
    public final FacebookAuthor from;
    public final String message;

    public FacebookStatus(String id, Date updatedTime, FacebookAuthor from, String message) {
        this.id = Objects.requireNonNull(id);
        this.updatedTime = Objects.requireNonNull(updatedTime);
        this.from = Objects.requireNonNull(from);
        this.message = Objects.requireNonNull(message);
    }

    public static FacebookStatus parse(JsonNode json, FacebookAPI api, String accessToken) throws RestParseException {
        Objects.requireNonNull(json);
        Objects.requireNonNull(api);
        Objects.requireNonNull(accessToken);

        return PARSER.parse(json, api, accessToken);
    }

    private static class FacebookStatusParser extends FacebookEntityParser<FacebookStatus> {

        private static final Logger LOGGER = LogManager.getLogger(FacebookStatusParser.class);

        @Override
        public FacebookStatus parse(JsonNode json, FacebookAPI api, String accessToken) throws RestParseException {
            Objects.requireNonNull(json);
            Objects.requireNonNull(api);
            Objects.requireNonNull(accessToken);

            String id = getAttribute(json, "id").asText();
            FacebookAuthor from = FacebookAuthor.parse(getAttribute(json, "from"), api, accessToken);
            String message = getAttribute(json, "message").asText();

            Date updated_time;

            try {
                updated_time = DATE_FORMAT.parse(getAttribute(json, "updated_time").asText());
            } catch (ParseException e) {
                LOGGER.error("Unable to parse updated_time attribute: " + getAttribute(json, "updated_time"));
                updated_time = new Date();
            }

            return new FacebookStatus(id, updated_time, from, message);
        }
    }
}
