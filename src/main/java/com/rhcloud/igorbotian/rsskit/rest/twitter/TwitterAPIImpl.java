package com.rhcloud.igorbotian.rsskit.rest.twitter;

import com.rhcloud.igorbotian.rsskit.db.RsskitDataSource;
import com.rhcloud.igorbotian.rsskit.db.twitter.TwitterEntityManager;
import com.rhcloud.igorbotian.rsskit.db.twitter.TwitterEntityManagerImpl;
import com.rhcloud.igorbotian.rsskit.rest.OAuth10Credentials;

import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterAPIImpl implements TwitterAPI {

    private final String consumerKey;
    private final String consumerSecret;
    private final OAuthEndpoint oAuth;
    private final TwitterEntityManager entityManager;

    public TwitterAPIImpl(OAuth10Credentials credentials, RsskitDataSource dataSource) throws TwitterException {
        Objects.requireNonNull(credentials);
        Objects.requireNonNull(dataSource);

        this.consumerKey = credentials.consumerKey;
        this.consumerSecret = credentials.consumerSecret;
        this.oAuth = new OAuthEndpoint(credentials);

        try {
            this.entityManager = new TwitterEntityManagerImpl(dataSource);
        } catch (SQLException e) {
            throw new TwitterException("Failed to initialize Twitter entity manager", e);
        }
    }

    @Override
    public URL getAuthorizationURL(URL callbackURL) throws TwitterException {
        Objects.requireNonNull(callbackURL);
        return oAuth.getAuthorizationURL(callbackURL);
    }

    @Override
    public String requestAccessToken(String oauthVerifier) throws TwitterException {
        Objects.requireNonNull(oauthVerifier);

        TwitterAccessToken accessToken = oAuth.requestAccessToken(oauthVerifier);
        return entityManager.registerAccessToken(accessToken);
    }

    @Override
    public TwitterTimeline getHomeTimeline(String token) throws TwitterException {
        Objects.requireNonNull(token);

        TwitterAccessToken accessToken = getAccessToken(token);
        OAuth10Credentials credentials = new OAuth10Credentials(consumerKey, consumerSecret,
                accessToken.accessToken, accessToken.tokenSecret);
        String sinceID = entityManager.getHomeTimelineSinceID(token);
        TwitterTimeline timeline = new HomeTimelineEndpoint(credentials).get(sinceID);

        updateSinceID(token, timeline);

        return timeline;
    }

    private void updateSinceID(String token, TwitterTimeline timeline) throws TwitterException {
        assert token != null;
        assert timeline != null;

        if (timeline.tweets.size() <= 1) { // at least one item is always returned by rsskit
            return;
        }

        String sinceID = timeline.tweets.get(1).id;
        entityManager.setHomeTimelineSinceID(token, sinceID);
    }

    private TwitterAccessToken getAccessToken(String token) throws TwitterException {
        assert token != null;

        TwitterAccessToken accessToken = entityManager.getAccessToken(token);

        if (accessToken == null) {
            throw new TwitterException("Specified access token is not registered: " + token);
        }

        return accessToken;
    }
}
