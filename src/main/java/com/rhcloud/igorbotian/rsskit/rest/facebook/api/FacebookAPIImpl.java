package com.rhcloud.igorbotian.rsskit.rest.facebook.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.db.RsskitDataSource;
import com.rhcloud.igorbotian.rsskit.db.facebook.FacebookEntityManager;
import com.rhcloud.igorbotian.rsskit.db.facebook.FacebookEntityManagerImpl;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookFeedItem;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookNotification;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookPost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class FacebookAPIImpl implements FacebookAPI {

    private static final Logger LOGGER = LogManager.getLogger(FacebookAPIImpl.class);
    private static final String API_VERSION = "2.3";

    private final OAuthEndpoint oAuth = new OAuthEndpoint();
    private final HomeEndpoint home;
    private final ObjectEndpoint objects;
    private final NotificationsEndpoint notifications;
    private final FacebookEntityManager entityManager;

    public FacebookAPIImpl(RsskitDataSource source) throws FacebookException {
        Objects.requireNonNull(source);

        try {
            this.entityManager = new FacebookEntityManagerImpl(source);
        } catch (SQLException e) {
            throw new FacebookException("Failed to initialize Facebook entity manager");
        }

        this.home = new HomeEndpoint(this);
        this.objects = new ObjectEndpoint(this);
        this.notifications = new NotificationsEndpoint(this);
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
    public List<FacebookFeedItem> getNewsFeed(String token) throws FacebookException {
        Objects.requireNonNull(token);

        String accessToken = entityManager.getAccessToken(token);

        if (accessToken == null) {
            throw new FacebookException("Access token is not registered: " + token);
        }

        Date since = entityManager.getSince(token);
        List<FacebookFeedItem> feed = home.getNewsFeed(accessToken, since);

        // always returning at least one post
        if (feed.size() > 1) {
            FacebookFeedItem post = feed.get(1);
            entityManager.setSince(token, post.post.createdTime);
        }

        return feed;
    }

    @Override
    public List<FacebookNotification> getNotifications(String token, Integer limit) throws FacebookException {
        Objects.requireNonNull(token);

        String accessToken = entityManager.getAccessToken(token);

        if (accessToken == null) {
            throw new FacebookException("Access token is not registered: " + token);
        }

        List<IncompleteFacebookNotification> incompleteNotifications = this.notifications.get(accessToken, limit);
        List<FacebookNotification> notifications = new ArrayList<>(incompleteNotifications.size());

        for(IncompleteFacebookNotification incompleteNotification : incompleteNotifications) {
            try {
                notifications.add(makeNotificationComplete(incompleteNotification, accessToken));
            } catch (FacebookException e) {
                LOGGER.error("Failed to obtain an object of a specified notification: "
                        + incompleteNotification.id, e);
            }
        }

        for(FacebookNotification notification : notifications) {
            if(notification.unread) {
                try {
                    this.notifications.markAsRead(accessToken, notification);
                } catch (FacebookException e) {
                    LOGGER.error("Failed to mark a specified notification as read: " + notification.link, e);
                }
            }
        }

        return notifications;
    }

    private FacebookNotification makeNotificationComplete(IncompleteFacebookNotification notification,
                                                          String accessToken) throws FacebookException {

        assert notification != null;
        assert accessToken != null;

        FacebookPost post = null;

        if(notification.isObjectComplete()) {
            try {
                post = FacebookPost.parse(notification.object);
            } catch (RestParseException e) {
                LOGGER.warn("Failed to parse a notification object: " + notification.id, e);
            }
        }

        if(post == null) {
            if(notification.isObjectIdentified()) {
                try {
                    post = FacebookPost.parse(objects.get(notification.getObjectID(), accessToken));
                } catch (RestParseException e) {
                    throw new FacebookException("Failed to get a Facebook object with a specified ID: "
                            + notification.getObjectID(), e);
                }
            } else {
                throw new FacebookException("Notifications object has no ID attribute: " + notification.id);
            }
        }

        return new FacebookNotification(
                notification.id,
                notification.from,
                notification.createdTime,
                notification.title,
                notification.link,
                notification.unread,
                post
        );
    }

    @Override
    public JsonNode getObject(String id, String accessToken) throws FacebookException {
        Objects.requireNonNull(id);
        return objects.get(id, accessToken);
    }
}
