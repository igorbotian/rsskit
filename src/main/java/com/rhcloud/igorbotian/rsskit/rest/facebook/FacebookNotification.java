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
public class FacebookNotification implements FacebookObject {

    private static final FacebookEntityParser<FacebookNotification> PARSER = new FacebookNotificationParser();

    public final String id;
    public final FacebookAuthor from;
    public final Date createdTime;
    public final String title;
    public final String link;
    public final boolean unread;
    public final FacebookPost post;

    public FacebookNotification(String id, FacebookAuthor from, Date createdTime, String title, String link,
                                boolean unread, FacebookPost post) {
        this.id = Objects.requireNonNull(id);
        this.from = Objects.requireNonNull(from);
        this.createdTime = Objects.requireNonNull(createdTime);
        this.title = Objects.requireNonNull(title);
        this.link = Objects.requireNonNull(link);
        this.unread = unread;
        this.post = Objects.requireNonNull(post);
    }

    public static FacebookNotification parse(JsonNode json, FacebookAPI api, String accessToken)
            throws RestParseException {

        Objects.requireNonNull(json);
        Objects.requireNonNull(api);
        Objects.requireNonNull(accessToken);

        return PARSER.parse(json, api, accessToken);
    }

    private static class FacebookNotificationParser extends FacebookEntityParser<FacebookNotification> {

        private static final Logger LOGGER = LogManager.getLogger(FacebookNotificationParser.class);

        @Override
        public FacebookNotification parse(JsonNode json, FacebookAPI api, String accessToken) throws RestParseException {
            Objects.requireNonNull(json);
            Objects.requireNonNull(api);
            Objects.requireNonNull(accessToken);

            String id = getAttribute(json, "id").asText();
            String title = getAttribute(json, "title").asText();
            String link = getAttribute(json, "link").asText();
            boolean unread = getAttribute(json, "unread").asBoolean();
            FacebookAuthor from = FacebookAuthor.parse(getAttribute(json, "from"), api, accessToken);
            FacebookPost post = FacebookPost.parse(getAttribute(json, "object"), api, accessToken);
            Date createdTime;

            try {
                createdTime = DATE_FORMAT.parse(getAttribute(json, "created_time").asText());
            } catch (ParseException e) {
                LOGGER.error("Unable to parse created_time attribute: " + getAttribute(json, "created_time"));
                createdTime = new Date();
            }

            return new FacebookNotification(id, from, createdTime, title, link, unread, post);
        }
    }
}
