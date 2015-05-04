package com.rhcloud.igorbotian.rsskit.rest.instagram;

import java.net.URL;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public interface InstagramAPI {

    URL getAuthorizationURL(String clientID, URL callbackURL) throws InstagramException;

    String requestAccessToken(String clientID, String clientSecret, String authorizationCode, URL callbackURL)
            throws InstagramException;

    InstagramFeed getSelfFeed(String accessToken) throws InstagramException;
}
