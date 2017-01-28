package com.rhcloud.igorbotian.rsskit.rest.facebook;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian
 */
public class FacebookStatus extends FacebookPost {

    public FacebookStatus(String id, Date createdTime, FacebookProfile from,
                          String caption, String message, FacebookPost source) {

        super(id, createdTime, from, caption, message, FacebookPostType.STATUS, source);
    }

    @Override
    public FacebookStatus asRepostOf(FacebookPost source) {
        Objects.requireNonNull(source);
        return new FacebookStatus(id, createdTime, from, caption, message, source);
    }
}
