package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.rhcloud.igorbotian.rsskit.db.RsskitDataSource;
import com.rhcloud.igorbotian.rsskit.db.vk.VkEntityManager;
import com.rhcloud.igorbotian.rsskit.db.vk.VkEntityManagerImpl;

import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkAPIImpl implements VkAPI {

    private static final String API_VERSION = "5.30";

    private final OAuthEndpoint oAuth = new OAuthEndpoint();
    private final NewsFeedEndpoint newsFeed = new NewsFeedEndpoint(API_VERSION);
    private final VkEntityManager entityManager;

    public VkAPIImpl(RsskitDataSource source) throws VkException {
        Objects.requireNonNull(source);

        try {
            this.entityManager = new VkEntityManagerImpl(source);
        } catch(SQLException e) {
            throw new VkException("Failed to initialize VK entity manager", e);
        }
    }

    @Override
    public String version() {
        return API_VERSION;
    }

    @Override
    public URL getAuthorizationURL(String clientID, Set<String> permissions) throws VkException {
        Objects.requireNonNull(clientID);
        Objects.requireNonNull(permissions);

        return oAuth.getAuthorizationURL(clientID, permissions, version());
    }

    @Override
    public String requestAccessToken(String clientID, String clientSecret, String code)
            throws VkException {

        Objects.requireNonNull(clientID);
        Objects.requireNonNull(clientSecret);
        Objects.requireNonNull(code);

        String accessToken = oAuth.requestAccessToken(clientID, clientSecret, code);
        return entityManager.registerAccessToken(accessToken);
    }

    @Override
    public VkFeed getNewsFeed(String token) throws VkException {
        Objects.requireNonNull(token);

        String accessToken = getAccessToken(token);
        Long startTime = entityManager.getNewsFeedStartTime(token);
        VkFeed feed = newsFeed.get(accessToken, startTime);

        updateStartTime(token, feed);

        return feed;
    }

    private String getAccessToken(String token) throws VkException {
        assert token != null;

        String accessToken = entityManager.getAccessToken(token);

        if(accessToken == null) {
            throw new VkException("Specified access token is not registered: " + token);
        }

        return accessToken;
    }

    private void updateStartTime(String token, VkFeed feed) throws VkException {
        assert token != null;
        assert feed != null;

        if(feed.items.isEmpty()) {
            return;
        }

        long startTime = feed.items.get(0).date.getTime(); // latter post is always returned by rsskit
        entityManager.setNewsFeedStartTime(token, startTime);
    }
}
