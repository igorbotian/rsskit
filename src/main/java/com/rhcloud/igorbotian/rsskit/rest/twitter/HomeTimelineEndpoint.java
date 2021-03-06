package com.rhcloud.igorbotian.rsskit.rest.twitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.OAuth10Credentials;
import com.rhcloud.igorbotian.rsskit.rest.OAuth10RestGetEndpoint;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class HomeTimelineEndpoint extends OAuth10RestGetEndpoint {

    private static final String ENDPOINT_URL = "https://api.twitter.com/1.1/statuses/home_timeline.json";
    private static final int MAX_ENTRIES = 50;

    HomeTimelineEndpoint(OAuth10Credentials credentials) {
        super(credentials);
    }

    public TwitterTimeline get(String sinceID) throws TwitterException {
        Set<NameValuePair> params = new HashSet<>();
        params.add(new BasicNameValuePair("count", Integer.toString(MAX_ENTRIES)));
        params.add(new BasicNameValuePair("trim_user", "false"));
        params.add(new BasicNameValuePair("exclude_replies", "true"));
        params.add(new BasicNameValuePair("include_entities", "true"));

        if (sinceID != null) {
            params.add(new BasicNameValuePair("since_id", sinceID));
        }

        try {
            JsonNode response = makeRequest(ENDPOINT_URL, params);
            return TwitterResponse.parse(response, new EntityParser<TwitterTimeline>() {

                @Override
                public TwitterTimeline parse(JsonNode json) throws RestParseException {
                    Objects.requireNonNull(json);
                    return TwitterTimeline.parse(json);
                }
            });
        } catch (IOException e) {
            throw new TwitterException("Failed to get Twitter home timeline", e);
        } catch (RestParseException e) {
            throw new TwitterException("Failed to parse Twitter response", e);
        }
    }
}
