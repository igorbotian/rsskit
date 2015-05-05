package com.rhcloud.igorbotian.rsskit.rest.vk;

import java.net.URL;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkAPIImpl implements VkAPI {

    private static final String API_VERSION = "5.30";

    private final OAuthEndpoint oAuth = new OAuthEndpoint();
    private final NewsFeedEndpoint newsFeed = new NewsFeedEndpoint(API_VERSION);

    @Override
    public String version() {
        return API_VERSION;
    }

    @Override
    public URL getAuthorizationURL(String clientID, Set<String> permissions) throws VkException {
        Objects.requireNonNull(clientID);
        Objects.requireNonNull(permissions);

        return oAuth.getAuthorizationURL(clientID, permissions, version());
    }

    @Override
    public String requestAccessToken(String clientID, String clientSecret, String code)
            throws VkException {

        Objects.requireNonNull(clientID);
        Objects.requireNonNull(clientSecret);
        Objects.requireNonNull(code);

        return oAuth.requestAccessToken(clientID, clientSecret, code);
    }

    @Override
    public VkFeed getNewsFeed(String accessToken) throws VkException {
        Objects.requireNonNull(accessToken);
        return newsFeed.get(accessToken);
    }
}
