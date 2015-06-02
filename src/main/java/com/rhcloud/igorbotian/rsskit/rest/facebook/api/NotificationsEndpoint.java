package com.rhcloud.igorbotian.rsskit.rest.facebook.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookNotification;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.*;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
class NotificationsEndpoint extends FacebookEndpoint {

    private static final String ENDPOINT = "me/notifications";
    private static final EntityParser<List<IncompleteFacebookNotification>> NOTIFICATIONS_PARSER
            = new IncompleteFacebookNotificationsParser();

    public NotificationsEndpoint(FacebookAPI api) {
        super(api);
    }

    public List<IncompleteFacebookNotification> get(String accessToken, Integer limit) throws FacebookException {
        if(limit != null && limit < 1) {
            throw new IllegalArgumentException("Limit should have a positive value: " + limit);
        }

        Objects.requireNonNull(accessToken);

        Set<NameValuePair> params = new HashSet<>();
        params.add(new BasicNameValuePair("access_token", accessToken));

        if(limit != null) {
            params.add(new BasicNameValuePair("limit", Integer.toString(limit)));
        }

        params.add(dateInUNIXTimeFormat());

        return makeGetRequest(ENDPOINT, params, NOTIFICATIONS_PARSER);
    }

    public void markAsRead(String accessToken, FacebookNotification notification) throws FacebookException {
        Objects.requireNonNull(accessToken);
        Objects.requireNonNull(notification);

        Set<NameValuePair> params = new HashSet<>();
        params.add(new BasicNameValuePair("access_token", accessToken));

        JsonNode response = makeRawJsonPostRequest(notification.id + "?unread=false", params);
        FacebookResponse.throwExceptionIfError(response);
    }

    private static class IncompleteFacebookNotificationsParser extends EntityParser<List<IncompleteFacebookNotification>> {

        @Override
        public List<IncompleteFacebookNotification> parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            List<IncompleteFacebookNotification> notifications = new ArrayList<>(json.size());

            for(int i = 0; i < json.size(); i++) {
                notifications.add(IncompleteFacebookNotification.parse(json.get(i)));
            }

            return notifications;
        }
    }
}
