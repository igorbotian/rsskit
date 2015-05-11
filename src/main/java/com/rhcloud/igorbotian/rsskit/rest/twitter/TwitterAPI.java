package com.rhcloud.igorbotian.rsskit.rest.twitter;

import java.net.URL;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public interface TwitterAPI {

    URL getAuthorizationURL(URL callbackURL) throws TwitterException;

    String requestAccessToken(String oauthVerifier) throws TwitterException;

    TwitterTimeline getHomeTimeline(String token) throws TwitterException;
}
