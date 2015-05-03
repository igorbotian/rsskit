package com.rhcloud.igorbotian.rsskit.rest.buffer;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestGetEndpoint;
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
class ProfilesEndpoint extends RestGetEndpoint {

    private static final String ENDPOINT_URL = "https://api.bufferapp.com/1/profiles.json";

    public List<String> getIDs(String accessToken) throws BufferException {
        Objects.requireNonNull(accessToken);

        try {
            JsonNode response = makeRequest(accessToken);
            return parseProfileIds(response);
        } catch (IOException e) {
            throw new BufferException("Failed to make request to the Buffer Profiles endpoint", e);
        }
    }

    private JsonNode makeRequest(String accessToken) throws IOException, BufferException {
        assert accessToken != null;

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("access_token", accessToken));

        return makeRequest(new URL(ENDPOINT_URL), params);
    }

    private List<String> parseProfileIds(JsonNode response) throws BufferException {
        assert response != null;

        List<String> ids = new ArrayList<>();

        for (int i = 0; i < response.size(); i++) {
            JsonNode profile = response.get(i);

            if (profile.has("id")) {
                ids.add(profile.get("id").asText());
            }
        }

        return ids;
    }
}
