package com.rhcloud.igorbotian.rsskit.rest.facebook;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestGetEndpoint;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.RestPostEndpoint;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
class NotificationsEndpoint extends RestGetEndpoint {

    private static final String GRAPH_API_URL = "https://graph.facebook.com/v%s";
    private static final String ENDPOINT_URL = GRAPH_API_URL + "/%s/notifications";
    private final String apiVersion;
    private final FacebookAPI api;
    private final RestPostEndpoint post = new RestPostEndpoint();

    public NotificationsEndpoint(String apiVersion, FacebookAPI api) {
        this.apiVersion = Objects.requireNonNull(apiVersion);
        this.api = Objects.requireNonNull(api);
    }

    public FacebookNotifications get(String userID, String accessToken) throws FacebookException {
        Objects.requireNonNull(userID);
        Objects.requireNonNull(accessToken);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("access_token", accessToken));

        try {
            JsonNode response = makeRequest(String.format(ENDPOINT_URL, apiVersion, userID), params);
            return parseNotifications(response, api, accessToken);
        } catch (IOException e) {
            throw new FacebookException("Failed to retrieve notifications list of a specified Facebook user: "
                    + userID, e);
        } catch (RestParseException e) {
            throw new FacebookException("Failed to parse Facebook response", e);
        }
    }

    private FacebookNotifications parseNotifications(JsonNode json, FacebookAPI api, final String accessToken)
            throws RestParseException, FacebookException {

        assert json != null;
        assert api != null;
        assert accessToken != null;

        return FacebookResponse.parse(json, api, accessToken, new FacebookEntityParser<FacebookNotifications>() {

            @Override
            public FacebookNotifications parse(JsonNode json, FacebookAPI api, String accessToken)
                    throws RestParseException {

                return FacebookNotifications.parse(json, api, accessToken);
            }
        });
    }

    public void markAsRead(String notificationId, String accessToken) throws FacebookException {
        Objects.requireNonNull(notificationId);
        Objects.requireNonNull(accessToken);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("access_token", accessToken));

        try {
            JsonNode response = post.makeRequest(
                    String.format(GRAPH_API_URL + "/%s?unread=false", api.version(), notificationId),
                    params
            );

            if(response.has("success")) {
                if(!response.get("success").asBoolean()) {
                    throw new FacebookException("Marking a notification with the specified ID was not successful: "
                            + notificationId);
                }
            } else {
                throw new FacebookException("Unexpected response returned by Facebook: " + response.toString());
            }
        } catch (IOException e) {
            throw new FacebookException("Failed to mark a notification with a specified ID as read: "
                    + notificationId, e);
        }
    }
}
