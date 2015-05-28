package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestGetEndpoint;
import com.rhcloud.igorbotian.rsskit.utils.URLUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class OAuthEndpoint extends RestGetEndpoint {

    private static final String AUTHORIZE_ENDPOINT_URL = "https://oauth.vk.com/authorize";
    private static final String ACCESS_TOKEN_URL = "https://oauth.vk.com/access_token";
    private static final String CALLBACK_URL = "https://oauth.vk.com/blank.html";

    public URL getAuthorizationURL(String clientID, Set<String> permissions, String apiVersion)
            throws VkException {

        Objects.requireNonNull(clientID);
        Objects.requireNonNull(permissions);
        Objects.requireNonNull(apiVersion);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("client_id", clientID));
        params.add(new BasicNameValuePair("scope", StringUtils.join(permissions.iterator(), ',')));
        params.add(new BasicNameValuePair("redirect_uri", CALLBACK_URL));
        params.add(new BasicNameValuePair("v", apiVersion));
        params.add(new BasicNameValuePair("state", "rsskit" /* any */));
        params.add(new BasicNameValuePair("response_type", "code"));

        try {
            return URLUtils.makeURL(AUTHORIZE_ENDPOINT_URL, params);
        } catch (MalformedURLException e) {
            throw new VkException("Failed to compose VK authorization URL", e);
        }
    }

    public String requestAccessToken(String clientID, String clientSecret, String code)
            throws VkException {

        Objects.requireNonNull(clientID);
        Objects.requireNonNull(clientSecret);
        Objects.requireNonNull(code);

        Set<NameValuePair> params = new HashSet<>();
        params.add(new BasicNameValuePair("client_id", clientID));
        params.add(new BasicNameValuePair("client_secret", clientSecret));
        params.add(new BasicNameValuePair("code", code));
        params.add(new BasicNameValuePair("redirect_uri", CALLBACK_URL));

        try {
            JsonNode response = makeRequest(ACCESS_TOKEN_URL, params);
            return parseAccessToken(response);
        } catch (IOException e) {
            throw new VkException("Failed to request VK access token", e);
        }
    }

    private String parseAccessToken(JsonNode json) throws VkException {
        assert json != null;

        if(!json.has("access_token")) {
            throw new VkException("No access token returned by VK");
        }

        return json.get("access_token").asText();
    }
}
