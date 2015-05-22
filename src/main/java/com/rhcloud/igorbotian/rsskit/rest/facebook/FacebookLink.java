package com.rhcloud.igorbotian.rsskit.rest.facebook;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class FacebookLink extends FacebookPost {

    public final String description;
    public final String link;
    public final String name;
    public final String picture;

    public FacebookLink(String id, Date createdTime, FacebookProfile from, String caption,
                        String message, String description, String link, String name, String picture) {

        super(id, createdTime, from, caption, message, FacebookPostType.LINK);

        this.description = Objects.requireNonNull(description);
        this.link = Objects.requireNonNull(link);
        this.name = Objects.requireNonNull(name);
        this.picture = Objects.requireNonNull(picture);
    }
}
