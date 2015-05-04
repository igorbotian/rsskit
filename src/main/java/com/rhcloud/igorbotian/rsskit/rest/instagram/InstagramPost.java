package com.rhcloud.igorbotian.rsskit.rest.instagram;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class InstagramPost {

    public final String url;
    public final String thumbnailURL;
    public final String comment;
    public final Date date;
    public final InstagramUser author;

    public InstagramPost(String url, String thumbnailURL, String comment, Date date, InstagramUser author) {
        this.url = Objects.requireNonNull(url);
        this.thumbnailURL = Objects.requireNonNull(thumbnailURL);
        this.comment = Objects.requireNonNull(comment);
        this.date = Objects.requireNonNull(date);
        this.author = Objects.requireNonNull(author);
    }
}
