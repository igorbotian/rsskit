package com.rhcloud.igorbotian.rsskit.rest.vk;

import java.net.URL;
import java.util.Set;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public interface VkAPI {

    String version();

    URL getAuthorizationURL(String clientID, Set<String> permissions) throws VkException;

    String requestAccessToken(String clientID, String clientSecret, String code) throws VkException;

    VkFeed getNewsFeed(String accessToken) throws VkException;
}
