package com.rhcloud.igorbotian.rsskit.rest.buffer;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestPostEndpoint;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class OAuthTokenEndpoint extends RestPostEndpoint {

    private static final String ENDPOINT_URL = "https://api.bufferapp.com/1/oauth2/token.json";

    public String getAccessToken(String clientID, String clientSecret, URL redirectURI,
                                 String authorizationCode) throws BufferException {

        Objects.requireNonNull(clientID);
        Objects.requireNonNull(clientSecret);
        Objects.requireNonNull(redirectURI);
        Objects.requireNonNull(authorizationCode);

        try {
            JsonNode response = makeRequest(clientID, clientSecret, redirectURI, authorizationCode);
            return parseAccessToken(response);
        } catch (IOException e) {
            throw new BufferException("Failed to make request to the Buffer OAuth endpoint", e);
        }
    }

    private JsonNode makeRequest(String clientID, String clientSecret, URL redirectURI, String authorizationCode)
            throws IOException, BufferException {
        assert clientID != null;
        assert clientSecret != null;
        assert redirectURI != null;
        assert authorizationCode != null;

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("client_id", clientID));
        params.add(new BasicNameValuePair("client_secret", clientSecret));
        params.add(new BasicNameValuePair("redirect_uri", redirectURI.toString()));
        params.add(new BasicNameValuePair("code", authorizationCode));
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));

        return makeRequest(ENDPOINT_URL, params);
    }

    private String parseAccessToken(JsonNode response) throws BufferException {
        assert response != null;

        if (!response.has("access_token")) {
            throw new BufferException("No access token returned by Buffer");
        }

        return response.get("access_token").asText();
    }
}
