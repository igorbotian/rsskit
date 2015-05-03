package com.rhcloud.igorbotian.rsskit.rest.twitter;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterTweet {

    public final String id;
    public final String text;
    public final Date date;
    public final TwitterUser author;
    public final List<String> attachments;

    public TwitterTweet(String text, Date date, String id, TwitterUser author, List<String> attachments) {
        this.text = Objects.requireNonNull(text);
        this.date = Objects.requireNonNull(date);
        this.id = Objects.requireNonNull(id);
        this.author = Objects.requireNonNull(author);
        this.attachments = Collections.unmodifiableList(Objects.requireNonNull(attachments));
    }
}
