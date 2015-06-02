package com.rhcloud.igorbotian.rsskit.rest.facebook;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class FacebookOffer extends FacebookPost {

    public FacebookOffer(String id, Date createdTime, FacebookProfile from, String caption, String message,
                         FacebookPost source) {
        super(id, createdTime, from, caption, message, FacebookPostType.OFFER, source);
    }

    @Override
    public FacebookOffer asRepostOf(FacebookPost source) {
        Objects.requireNonNull(source);
        return new FacebookOffer(id, createdTime, from, caption, message, source);
    }
}
