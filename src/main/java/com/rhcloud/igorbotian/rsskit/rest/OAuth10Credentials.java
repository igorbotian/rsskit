package com.rhcloud.igorbotian.rsskit.rest;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class OAuth10Credentials {

    public final String consumerKey;
    public final String consumerSecret;
    public final String accessToken;
    public final String tokenSecret;

    public OAuth10Credentials(String consumerKey, String consumerSecret) {
        this(consumerKey, consumerSecret, null, null);
    }

    public OAuth10Credentials(String consumerKey, String consumerSecret, String accessToken, String tokenSecret) {
        this.consumerKey = Objects.requireNonNull(consumerKey);
        this.consumerSecret = Objects.requireNonNull(consumerSecret);
        this.accessToken = accessToken;
        this.tokenSecret = tokenSecret;
    }

    public boolean areTokenAndSecretAvailable() {
        return accessToken != null && tokenSecret != null;
    }
}
