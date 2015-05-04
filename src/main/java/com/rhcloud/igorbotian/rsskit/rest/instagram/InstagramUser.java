package com.rhcloud.igorbotian.rsskit.rest.instagram;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class InstagramUser {

    public final String id;
    public final String username;
    public final String fullName;
    public final String profilePicture;

    public InstagramUser(String id, String username, String fullName, String profilePicture) {
        this.id = Objects.requireNonNull(id);
        this.username = Objects.requireNonNull(username);
        this.fullName = Objects.requireNonNull(fullName);
        this.profilePicture = Objects.requireNonNull(profilePicture);
    }
}
