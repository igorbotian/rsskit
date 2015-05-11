package com.rhcloud.igorbotian.rsskit.db.instagram;

import com.rhcloud.igorbotian.rsskit.rest.instagram.InstagramException;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public interface InstagramEntityManager {

    String registerAccessToken(String accessToken) throws InstagramException;

    String getAccessToken(String token) throws InstagramException;

    void setSelfFeedMinID(String token, String minID) throws InstagramException;

    String getSelfFeedMinID(String token) throws InstagramException;
}
