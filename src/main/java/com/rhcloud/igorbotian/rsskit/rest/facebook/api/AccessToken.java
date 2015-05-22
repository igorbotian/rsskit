package com.rhcloud.igorbotian.rsskit.rest.facebook.api;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
class AccessToken {

    public final String token;
    public final Date expires;

    AccessToken(String token, Date expires) {
        this.token = Objects.requireNonNull(token);
        this.expires = Objects.requireNonNull(expires);
    }
}
