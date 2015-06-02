package com.rhcloud.igorbotian.rsskit.rest.facebook;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class FacebookFeedItem {

    public final FacebookPost post;
    public final FacebookPost source;

    public FacebookFeedItem(FacebookPost post) {
        this(post, null);
    }

    public FacebookFeedItem(FacebookPost post, FacebookPost source) {
        this.post = Objects.requireNonNull(post);
        this.source = source;
    }

    public boolean isRepost() {
        return source != null;
    }
}
