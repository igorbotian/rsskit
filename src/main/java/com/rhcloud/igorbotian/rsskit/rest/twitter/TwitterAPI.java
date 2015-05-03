package com.rhcloud.igorbotian.rsskit.rest.twitter;

import com.rhcloud.igorbotian.rsskit.rest.OAuth10Credentials;

import java.net.URL;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public interface TwitterAPI {

    URL getAuthorizationURL(URL callbackURL) throws TwitterException;

    OAuth10Credentials requestAccessToken(String oauthVerifier) throws TwitterException;

    TwitterTimeline getHomeTimeline(OAuth10Credentials token) throws TwitterException;
}
