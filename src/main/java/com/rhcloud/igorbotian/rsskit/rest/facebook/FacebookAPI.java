package com.rhcloud.igorbotian.rsskit.rest.facebook;

import com.fasterxml.jackson.databind.JsonNode;

import java.net.URL;
import java.util.Set;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public interface FacebookAPI {

    String version();

    URL getAuthorizationURL(String clientID, Set<String> permissions, URL callbackURL) throws FacebookException;

    String requestAccessToken(String clientID, String clientSecret, String code, URL callbackURL) throws FacebookException;

    boolean isAccessTokenExpired(String token) throws FacebookException;

    FacebookNotifications getNotifications(String accessToken) throws FacebookException;

    JsonNode getObject(String id, String accessToken) throws FacebookException;
}
