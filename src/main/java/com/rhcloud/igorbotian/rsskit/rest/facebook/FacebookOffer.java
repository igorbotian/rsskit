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
public class FacebookOffer implements FacebookObject {

    private static final FacebookEntityParser<FacebookOffer> PARSER = new FacebookOfferParser();

    public final String id;
    public final Date createdTime;
    public final FacebookAuthor from;
    public final String message;

    public FacebookOffer(String id, Date createdTime, FacebookAuthor from, String message) {
        this.id = Objects.requireNonNull(id);
        this.createdTime = Objects.requireNonNull(createdTime);
        this.from = Objects.requireNonNull(from);
        this.message = Objects.requireNonNull(message);
    }

    public static FacebookOffer parse(JsonNode json, FacebookAPI api, String accessToken) throws RestParseException {
        Objects.requireNonNull(json);
        Objects.requireNonNull(api);
        Objects.requireNonNull(accessToken);

        return PARSER.parse(json, api, accessToken);
    }

    private static class FacebookOfferParser extends FacebookEntityParser<FacebookOffer> {

        private static final Logger LOGGER = LogManager.getLogger(FacebookOfferParser.class);

        @Override
        public FacebookOffer parse(JsonNode json, FacebookAPI api, String accessToken) throws RestParseException {
            Objects.requireNonNull(json);
            Objects.requireNonNull(api);
            Objects.requireNonNull(accessToken);

            String id = getAttribute(json, "id").asText();
            FacebookAuthor from = FacebookAuthor.parse(getAttribute(json, "from"), api, accessToken);
            String message = getAttribute(json, "message").asText();

            Date createdTime;

            try {
                createdTime = DATE_FORMAT.parse(getAttribute(json, "created_time").asText());
            } catch (ParseException e) {
                LOGGER.error("Unable to parse created_time attribute: " + getAttribute(json, "created_time"));
                createdTime = new Date();
            }

            return new FacebookOffer(id, createdTime, from, message);
        }
    }
}
