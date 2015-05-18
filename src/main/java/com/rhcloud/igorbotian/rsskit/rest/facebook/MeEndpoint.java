package com.rhcloud.igorbotian.rsskit.rest.facebook;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestGetEndpoint;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
class MeEndpoint extends RestGetEndpoint {

    private static final String ENDPOINT_URL = "https://graph.facebook.com/v%s/me";

    private final String apiVersion;

    public MeEndpoint(String apiVersion) {
        this.apiVersion = Objects.requireNonNull(apiVersion);
    }

    public String getID(FacebookAPI api, String accessToken) throws FacebookException {
        Objects.requireNonNull(accessToken);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("access_token", accessToken));

        try {
            JsonNode response = makeRequest(String.format(ENDPOINT_URL, apiVersion), params);
            return parseUserID(response, api, accessToken);
        } catch (IOException e) {
            throw new FacebookException("Failed to get user ID of a specified user", e);
        } catch (RestParseException e) {
            throw new FacebookException("Failed to parse FacebookRequest", e);
        }
    }

    private String parseUserID(JsonNode json, FacebookAPI api, String accessToken)
            throws RestParseException, FacebookException {

        assert json != null;
        assert api != null;
        assert accessToken != null;

        FacebookResponse.throwExceptionIfError(json);
        return FacebookAuthor.parse(json, api, accessToken).id;
    }
}
