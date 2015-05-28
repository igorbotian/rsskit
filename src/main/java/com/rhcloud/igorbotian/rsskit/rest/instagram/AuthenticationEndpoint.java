package com.rhcloud.igorbotian.rsskit.rest.instagram;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestPostEndpoint;
import com.rhcloud.igorbotian.rsskit.utils.URLUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class AuthenticationEndpoint extends RestPostEndpoint {

    private static final String AUTHORIZE_ENDPOINT_URL = "https://api.instagram.com/oauth/authorize/";
    private static final String ACCESS_TOKEN_ENDPOINT_URL = "https://api.instagram.com/oauth/access_token";

    public URL getAuthorizationURL(String clientID, URL callbackURL) throws InstagramException {
        Objects.requireNonNull(clientID);
        Objects.requireNonNull(callbackURL);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("client_id", clientID));
        params.add(new BasicNameValuePair("redirect_uri", callbackURL.toString()));
        params.add(new BasicNameValuePair("response_type", "code"));

        try {
            return URLUtils.makeURL(AUTHORIZE_ENDPOINT_URL, params);
        } catch (MalformedURLException e) {
            throw new InstagramException("Failed to compose Instagram authorization URL", e);
        }
    }

    public String requestAccessToken(String clientID, String clientSecret, String authorizationCode, URL callbackURL)
            throws InstagramException {

        Objects.requireNonNull(clientID);
        Objects.requireNonNull(clientSecret);
        Objects.requireNonNull(authorizationCode);
        Objects.requireNonNull(callbackURL);

        Set<NameValuePair> params = new HashSet<>();
        params.add(new BasicNameValuePair("client_id", clientID));
        params.add(new BasicNameValuePair("client_secret", clientSecret));
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("redirect_uri", callbackURL.toString()));
        params.add(new BasicNameValuePair("code", authorizationCode));

        try {
            JsonNode response = makeRequest(ACCESS_TOKEN_ENDPOINT_URL, params);
            return parseAccessToken(response);
        } catch (IOException e) {
            throw new InstagramException("Failed to request Instagram access token", e);
        }
    }

    private String parseAccessToken(JsonNode response) throws InstagramException {
        assert response != null;

        if (!response.has("access_token")) {
            throw new InstagramException("No access token returned by Instagram");
        }

        return response.get("access_token").asText();
    }
}
