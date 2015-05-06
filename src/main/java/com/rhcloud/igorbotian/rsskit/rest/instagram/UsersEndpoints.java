package com.rhcloud.igorbotian.rsskit.rest.instagram;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestGetEndpoint;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class UsersEndpoints extends RestGetEndpoint {

    private static final String SELF_FEED_ENDPOINT_URL = "https://api.instagram.com/v1/users/self/feed";
    private static final int MAX_ENTRIES = 100;

    public InstagramFeed getSelfFeed(String accessToken) throws InstagramException {
        Objects.requireNonNull(accessToken);

        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("access_token", accessToken));
            params.add(new BasicNameValuePair("count", Integer.toString(MAX_ENTRIES)));

            JsonNode response = makeRequest(SELF_FEED_ENDPOINT_URL, params);
            return InstagramResponse.parse(response, new EntityParser<InstagramFeed>() {

                @Override
                public InstagramFeed parse(JsonNode json) throws RestParseException {
                    Objects.requireNonNull(json);
                    return InstagramFeed.parse(json);
                }
            });
        } catch (IOException e) {
            throw new InstagramException("Failed to retrieve Instagram self feed", e);
        } catch (RestParseException e) {
            throw new InstagramException("Failed to parse Instagram response", e);
        }
    }
}
