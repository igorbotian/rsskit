package com.rhcloud.igorbotian.rsskit.rest.instagram;

import com.rhcloud.igorbotian.rsskit.db.RsskitDataSource;
import com.rhcloud.igorbotian.rsskit.db.instagram.InstagramEntityManager;
import com.rhcloud.igorbotian.rsskit.db.instagram.InstagramEntityManagerImpl;

import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class InstagramAPIImpl implements InstagramAPI {

    private final AuthenticationEndpoint authentication = new AuthenticationEndpoint();
    private final UsersEndpoints users = new UsersEndpoints();
    private final MediaEndpoints media = new MediaEndpoints();
    private final InstagramEntityManager entityManager;

    public InstagramAPIImpl(RsskitDataSource dataSource) throws InstagramException {
        Objects.requireNonNull(dataSource);

        try {
            this.entityManager = new InstagramEntityManagerImpl(dataSource);
        } catch (SQLException e) {
            throw new InstagramException("Failed to initialize Instagram entity manager", e);
        }
    }

    @Override
    public URL getAuthorizationURL(String clientID, URL callbackURL) throws InstagramException {
        Objects.requireNonNull(clientID);
        Objects.requireNonNull(callbackURL);

        return authentication.getAuthorizationURL(clientID, callbackURL);
    }

    @Override
    public String requestAccessToken(String clientID, String clientSecret, String authorizationCode, URL callbackURL)
            throws InstagramException {

        Objects.requireNonNull(clientID);
        Objects.requireNonNull(clientSecret);
        Objects.requireNonNull(authorizationCode);
        Objects.requireNonNull(callbackURL);

        String accessToken = authentication.requestAccessToken(clientID, clientSecret, authorizationCode, callbackURL);
        return entityManager.registerAccessToken(accessToken);
    }

    @Override
    public InstagramFeed getSelfFeed(String token) throws InstagramException {
        Objects.requireNonNull(token);

        String accessToken = getAccessToken(token);
        String minID = entityManager.getSelfFeedMinID(token);
        InstagramFeed feed = users.getSelfFeed(accessToken, minID);

        updateMinID(token, feed);

        return feed;
    }

    @Override
    public boolean isShortenURL(URL url) {
        Objects.requireNonNull(url);
        return media.isShortenURL(url);
    }

    @Override
    public URL unshortURL(URL url, String token) throws InstagramException {
        Objects.requireNonNull(url);
        Objects.requireNonNull(token);

        String accessToken = getAccessToken(token);
        return media.unshortURL(url, accessToken);
    }

    private void updateMinID(String token, InstagramFeed feed) throws InstagramException {
        assert token != null;
        assert feed != null;

        if(feed.posts.size() <= 1) { // at least one item is always returned by rsskit
           return;
        }

        String minID = feed.posts.get(1).id;
        entityManager.setSelfFeedMinID(token, minID);
    }

    private String getAccessToken(String token) throws InstagramException {
        assert token != null;

        String accessToken = entityManager.getAccessToken(token);

        if(accessToken == null) {
            throw new InstagramException("Specified access token is not registered: " + token);
        }

        return accessToken;
    }
}
