package com.rhcloud.igorbotian.rsskit.rest.buffer;

import org.apache.http.NameValuePair;

import java.net.URL;
import java.util.List;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public interface BufferAPI {

    URL authorize(String clientID, URL redirectURI) throws BufferException;

    String getAccessToken(String clientID, String clientSecret, URL redirectURI, String authorizationCode)
            throws BufferException;

    List<String> getProfilesIDs(String accessToken) throws BufferException;

    List<NameValuePair> getPendingUpdates(List<String> profileIDs, String accessToken, int count) throws BufferException;
}
