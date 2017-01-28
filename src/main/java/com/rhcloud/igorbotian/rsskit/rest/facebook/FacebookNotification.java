package com.rhcloud.igorbotian.rsskit.rest.facebook;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian
 */
public class FacebookNotification {

    public final String id;
    public final FacebookProfile from;
    public final Date createdTime;
    public final String title;
    public final String link;
    public final boolean unread;
    public final FacebookPost object;

    public FacebookNotification(String id, FacebookProfile from, Date createdTime, String title, String link,
                                boolean unread, FacebookPost object) {

        this.id = Objects.requireNonNull(id);
        this.from = Objects.requireNonNull(from);
        this.createdTime = Objects.requireNonNull(createdTime);
        this.title = Objects.requireNonNull(title);
        this.link = Objects.requireNonNull(link);
        this.unread = unread;
        this.object = Objects.requireNonNull(object);
    }
}
