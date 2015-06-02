package com.rhcloud.igorbotian.rsskit.rest.facebook;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public abstract class FacebookPost {

    public final String id;
    public final Date createdTime;
    public final FacebookProfile from;
    public final String caption;
    public final String message;
    public final FacebookPostType type;

    public FacebookPost(String id, Date createdTime, FacebookProfile from, String caption,
                        String message, FacebookPostType type) {

        this.id = Objects.requireNonNull(id);
        this.createdTime = Objects.requireNonNull(createdTime);
        this.from = Objects.requireNonNull(from);
        this.caption = Objects.requireNonNull(caption);
        this.message = Objects.requireNonNull(message);
        this.type = Objects.requireNonNull(type);
    }
}
