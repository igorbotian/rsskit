package com.rhcloud.igorbotian.rsskit.rest.facebook;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.db.RsskitDataSource;
import com.rhcloud.igorbotian.rsskit.db.facebook.FacebookEntityManager;
import com.rhcloud.igorbotian.rsskit.db.facebook.FacebookEntityManagerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class FacebookAPIImpl implements FacebookAPI {

    private static final Logger LOGGER = LogManager.getLogger(FacebookAPIImpl.class);
    private static final String API_VERSION = "2.3";

    private final OAuthEndpoint oAuth = new OAuthEndpoint();
    private final MeEndpoint me = new MeEndpoint(API_VERSION);
    private final NotificationsEndpoint notifications;
    private final ObjectEndpoint objects = new ObjectEndpoint(API_VERSION);
    private final FacebookEntityManager entityManager;

    public FacebookAPIImpl(RsskitDataSource source) throws FacebookException {
        Objects.requireNonNull(source);

        try {
            this.entityManager = new FacebookEntityManagerImpl(source);
        } catch (SQLException e) {
            throw new FacebookException("Failed to initialize Facebook entity manager");
        }

        this.notifications = new NotificationsEndpoint(API_VERSION, this);
    }

    @Override
    public String requestAccessToken(String clientID, String clientSecret, String code, URL callbackURL)
            throws FacebookException {

        Objects.requireNonNull(clientID);
        Objects.requireNonNull(clientSecret);
        Objects.requireNonNull(code);
        Objects.requireNonNull(callbackURL);

        AccessToken token = oAuth.requestAccessToken(clientID, clientSecret, code, callbackURL);
        return entityManager.registerAccessToken(token.token, token.expires);
    }

    @Override
    public URL getAuthorizationURL(String clientID, Set<String> permissions, URL callbackURL) throws FacebookException {
        Objects.requireNonNull(clientID);
        Objects.requireNonNull(permissions);
        Objects.requireNonNull(callbackURL);

        return oAuth.getAuthorizationURL(clientID, permissions, callbackURL);
    }

    @Override
    public String version() {
        return API_VERSION;
    }

    @Override
    public boolean isAccessTokenExpired(String token) throws FacebookException {
        Objects.requireNonNull(token);
        return entityManager.isAcessTokenExpired(token);
    }

    @Override
    public FacebookNotifications getNotifications(String token) throws FacebookException {
        Objects.requireNonNull(token);

        String accessToken = entityManager.getAccessToken(token);

        if (accessToken == null) {
            throw new FacebookException("Access token is not registered: " + token);
        }

        String userID = me.getID(this, accessToken);
        FacebookNotifications result = notifications.get(userID, accessToken);
        markNotificationsAsRead(result, accessToken);
        return result;
    }

    private void markNotificationsAsRead(FacebookNotifications notifications, String accessToken) {
        assert notifications != null;
        assert accessToken != null;

        try {
            for(int i = 1 ; i < notifications.items.size(); i++) { // first notification is always left unread
                FacebookNotification notification = notifications.items.get(i);

                if (notification.unread) {
                    markNotificationAsRead(notification.id, accessToken);
                }
            }
        } catch (FacebookException e) {
            LOGGER.error("Failed to mark notifications as read", e);
        }
    }

    @Override
    public void markNotificationAsRead(String id, String accessToken) throws FacebookException {
        Objects.requireNonNull(id);
        Objects.requireNonNull(accessToken);

        notifications.markAsRead(id, accessToken);
    }

    @Override
    public JsonNode getObject(String id, String accessToken) throws FacebookException {
        Objects.requireNonNull(id);
        return objects.get(id, accessToken);
    }
}
