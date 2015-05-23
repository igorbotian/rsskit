package com.rhcloud.igorbotian.rsskit.rest.facebook;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class FacebookPhoto extends FacebookPost {

    public final String name;
    public final String link;
    public final String image;
    public final String picture;

    public FacebookPhoto(String id, Date createdTime, FacebookProfile from, String caption,
                         String message, String name, String link, String image, String picture) {

        super(id, createdTime, from, caption, message, FacebookPostType.PHOTO);

        this.name = Objects.requireNonNull(name);
        this.link = Objects.requireNonNull(link);
        this.image = Objects.requireNonNull(image);
        this.picture = Objects.requireNonNull(picture);
    }
}
