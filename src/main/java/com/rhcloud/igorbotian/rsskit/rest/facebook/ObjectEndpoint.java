package com.rhcloud.igorbotian.rsskit.rest.facebook;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestGetEndpoint;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class ObjectEndpoint extends RestGetEndpoint {

    private static final String ENDPOINT_URL = "https://graph.facebook.com/v%s/%s";

    private final String apiVersion;

    public ObjectEndpoint(String apiVersion) {
        this.apiVersion = Objects.requireNonNull(apiVersion);
    }

    public JsonNode get(String id, String accessToken) throws FacebookException {
        Objects.requireNonNull(id);
        Objects.requireNonNull(accessToken);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("access_token", accessToken));

        try {
            JsonNode response = makeRequest(String.format(ENDPOINT_URL, apiVersion, id), params);
            FacebookResponse.throwExceptionIfError(response);
            return response;
        } catch (IOException e) {
            throw new FacebookException("Failed to get Facebook object by ID: " + id, e);
        }
    }
}
