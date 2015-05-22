package com.rhcloud.igorbotian.rsskit.rest.facebook;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class FacebookProfile {

    public final String id;
    public final String name;
    public final String link;

    public FacebookProfile(String id, String name, String link) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.link = Objects.requireNonNull(link);
    }
}
