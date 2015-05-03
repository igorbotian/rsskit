package com.rhcloud.igorbotian.rsskit.rest.twitter;

import com.rhcloud.igorbotian.rsskit.rest.OAuth10Credentials;
import com.rhcloud.igorbotian.rsskit.rest.OAuth10RestGetEndpoint;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;

import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class OAuthEndpoint extends OAuth10RestGetEndpoint {

    private static final String AUTHENTICATION_URL = "https://api.twitter.com/oauth/authorize";
    private static final String REQUEST_TOKEN_ENDPOINT_URL = "https://api.twitter.com/oauth/request_token";
    private static final String ACCESS_TOKEN_ENDPOINT_URL = "https://api.twitter.com/oauth/access_token";

    private final OAuthProvider provider;

    OAuthEndpoint(OAuth10Credentials credentials) {
        super(credentials);

        provider = new DefaultOAuthProvider(
                REQUEST_TOKEN_ENDPOINT_URL,
                ACCESS_TOKEN_ENDPOINT_URL,
                AUTHENTICATION_URL
        );
    }

    public URL getAuthorizationURL(URL callbackURL) throws TwitterException {
        Objects.requireNonNull(callbackURL);

        try {
            return new URL(provider.retrieveRequestToken(consumer, callbackURL.toString()));
        } catch (Exception e) {
            throw new TwitterException("Failed to compose Sign in with Twitter authorization page URL", e);
        }
    }

    public OAuth10Credentials requestAccessToken(String oauthVerifier) throws TwitterException {
        Objects.requireNonNull(oauthVerifier);

        try {
            provider.retrieveAccessToken(consumer, oauthVerifier);
            String token = consumer.getToken();
            String tokenSecret = consumer.getTokenSecret();
            return new OAuth10Credentials(token, tokenSecret);
        } catch (Exception e) {
            throw new TwitterException("Failed to request Twitter REST API access token", e);
        }
    }
}
