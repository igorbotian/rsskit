package com.rhcloud.igorbotian.rsskit.db.facebook;

import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookException;

import java.util.Date;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public interface FacebookEntityManager {

    String registerAccessToken(String accessToken, Date expiredDate) throws FacebookException;

    String getAccessToken(String token) throws FacebookException;

    boolean isAcessTokenExpired(String token) throws FacebookException;

    Date getSince(String token) throws FacebookException;

    void setSince(String token, Date since) throws FacebookException;
}
