package com.rhcloud.igorbotian.rsskit.rest.instagram;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class InstagramFeed {

    public final List<InstagramPost> posts;

    public InstagramFeed(List<InstagramPost> posts) {
        this.posts = Collections.unmodifiableList(Objects.requireNonNull(posts));
    }
}
