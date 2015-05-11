package com.rhcloud.igorbotian.rsskit.rest.twitter;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterAccessToken {

    public final String accessToken;
    public final String tokenSecret;

    public TwitterAccessToken(String accessToken, String tokenSecret) {
        Objects.requireNonNull(tokenSecret);
        Objects.requireNonNull(accessToken);

        this.tokenSecret = tokenSecret;
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return String.format("{access token = %s, token secret = %s}", tokenSecret, accessToken);
    }
}
