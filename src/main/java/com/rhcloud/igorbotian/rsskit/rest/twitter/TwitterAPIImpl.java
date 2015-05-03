package com.rhcloud.igorbotian.rsskit.rest.twitter;

import com.rhcloud.igorbotian.rsskit.rest.OAuth10Credentials;

import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterAPIImpl implements TwitterAPI {

    private final OAuthEndpoint oAuth;

    public TwitterAPIImpl(OAuth10Credentials credentials) {
        this.oAuth = new OAuthEndpoint(Objects.requireNonNull(credentials));
    }

    @Override
    public URL getAuthorizationURL(URL callbackURL) throws TwitterException {
        return oAuth.getAuthorizationURL(Objects.requireNonNull(callbackURL));
    }

    @Override
    public OAuth10Credentials requestAccessToken(String oauthVerifier) throws TwitterException {
        return oAuth.requestAccessToken(Objects.requireNonNull(oauthVerifier));
    }

    @Override
    public TwitterTimeline getHomeTimeline(OAuth10Credentials token) throws TwitterException {
        return new HomeTimelineEndpoint(token).get();
    }
}
