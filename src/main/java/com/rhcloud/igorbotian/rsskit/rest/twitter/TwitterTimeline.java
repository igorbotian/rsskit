package com.rhcloud.igorbotian.rsskit.rest.twitter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterTimeline {

    public final List<TwitterTweet> tweets;

    public TwitterTimeline(List<TwitterTweet> tweets) {
        this.tweets = Collections.unmodifiableList(Objects.requireNonNull(tweets));
    }
}
