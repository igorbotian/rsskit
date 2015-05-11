package com.rhcloud.igorbotian.rsskit.db.twitter;

import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterAccessToken;
import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterException;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public interface TwitterEntityManager {

    String registerAccessToken(TwitterAccessToken accessToken) throws TwitterException;

    TwitterAccessToken getAccessToken(String token) throws TwitterException;

    void setHomeTimelineSinceID(String token, String sinceID) throws TwitterException;

    String getHomeTimelineSinceID(String token) throws TwitterException;
}
