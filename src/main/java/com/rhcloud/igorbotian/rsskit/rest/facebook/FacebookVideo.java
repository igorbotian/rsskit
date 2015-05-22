package com.rhcloud.igorbotian.rsskit.rest.facebook;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class FacebookVideo extends FacebookPost {

    public final String name;
    public final String description;
    public final String picture;
    public final String source;
    public final String embedHTML;

    public FacebookVideo(String id, Date createdTime, FacebookProfile from, String caption, String message,
                         String name, String description, String picture, String source, String embedHTML) {

        super(id, createdTime, from, caption, message, FacebookPostType.VIDEO);

        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.picture = Objects.requireNonNull(picture);
        this.source = Objects.requireNonNull(source);
        this.embedHTML = Objects.requireNonNull(embedHTML);
    }
}
