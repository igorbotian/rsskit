package com.rhcloud.igorbotian.rsskit.rest.facebook;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class FacebookRepost implements FacebookNewsFeedItem {

    public final FacebookPost post;
    public final FacebookPost repost;

    public FacebookRepost(FacebookPost post, FacebookPost repost) {
        this.post = Objects.requireNonNull(post);
        this.repost = Objects.requireNonNull(repost);
    }
}
