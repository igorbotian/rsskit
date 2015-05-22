package com.rhcloud.igorbotian.rsskit.rest.facebook;

import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class FacebookNewsFeed {

    public final List<FacebookNewsFeedItem> posts;

    public FacebookNewsFeed(List<FacebookNewsFeedItem> posts) {
        this.posts = Objects.requireNonNull(posts);
    }
}
