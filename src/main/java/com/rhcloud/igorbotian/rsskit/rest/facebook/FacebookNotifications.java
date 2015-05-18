package com.rhcloud.igorbotian.rsskit.rest.facebook;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class FacebookNotifications implements FacebookObject {

    private static final FacebookEntityParser<FacebookNotifications> PARSER = new FacebookNotificationsParser();

    public final List<FacebookNotification> items;

    public FacebookNotifications(List<FacebookNotification> items) {
        this.items = Objects.requireNonNull(items);
    }

    public static FacebookNotifications parse(JsonNode json, FacebookAPI api, String accessToken)
            throws RestParseException {

        Objects.requireNonNull(json);
        Objects.requireNonNull(api);
        Objects.requireNonNull(accessToken);

        return PARSER.parse(json, api, accessToken);
    }

    private static class FacebookNotificationsParser extends FacebookEntityParser<FacebookNotifications> {

        @Override
        public FacebookNotifications parse(JsonNode json, FacebookAPI api, String accessToken) throws RestParseException {
            Objects.requireNonNull(json);
            Objects.requireNonNull(api);
            Objects.requireNonNull(accessToken);

            List<FacebookNotification> notifications = new ArrayList<>(json.size());

            for (int i = 0; i < json.size(); i++) {
                notifications.add(FacebookNotification.parse(json.get(i), api, accessToken));
            }

            return new FacebookNotifications(notifications);
        }
    }
}
