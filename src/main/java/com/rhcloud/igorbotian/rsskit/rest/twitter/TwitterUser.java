package com.rhcloud.igorbotian.rsskit.rest.twitter;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterUser {

    public final String name;
    public final String screenName;
    public final String profileImageURL;

    public TwitterUser(String name, String screenName, String profileImageURL) {
        this.name = name;
        this.screenName = screenName;
        this.profileImageURL = profileImageURL;
    }
}
