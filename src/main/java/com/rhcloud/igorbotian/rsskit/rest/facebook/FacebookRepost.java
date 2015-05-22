package com.rhcloud.igorbotian.rsskit.rest.facebook;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class FacebookRepost implements FacebookNewsFeedItem {

    public final String id;
    public final Date createdTime;
    public final FacebookProfile from;
    public final FacebookPost repost;

    public FacebookRepost(String id, Date createdTime, FacebookProfile from, FacebookPost repost) {
        this.id = Objects.requireNonNull(id);
        this.createdTime = Objects.requireNonNull(createdTime);
        this.from = Objects.requireNonNull(from);
        this.repost = Objects.requireNonNull(repost);
    }
}
