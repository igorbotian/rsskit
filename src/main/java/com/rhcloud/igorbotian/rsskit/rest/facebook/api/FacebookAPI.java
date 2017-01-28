package com.rhcloud.igorbotian.rsskit.rest.facebook.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookNotification;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookPost;

import java.net.URL;
import java.util.List;
import java.util.Set;

/**
 * @author Igor Botian
 */
public interface FacebookAPI {

    String version();

    URL getAuthorizationURL(String clientID, Set<String> permissions, URL callbackURL) throws FacebookException;

    String requestAccessToken(String clientID, String clientSecret, String code, URL callbackURL) throws FacebookException;

    boolean isAccessTokenExpired(String token) throws FacebookException;

    List<FacebookPost> getNewsFeed(String token) throws FacebookException;

    List<FacebookNotification> getNotifications(String token, Integer limit) throws FacebookException;

    List<FacebookPost> getPostsFromNotifications(String token, Integer limit) throws FacebookException;

    JsonNode getObject(String id, String accessToken) throws FacebookException;
}
