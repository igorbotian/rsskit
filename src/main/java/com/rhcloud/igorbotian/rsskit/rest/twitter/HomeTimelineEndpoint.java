package com.rhcloud.igorbotian.rsskit.rest.twitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.OAuth10Credentials;
import com.rhcloud.igorbotian.rsskit.rest.OAuth10RestGetEndpoint;
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
class HomeTimelineEndpoint extends OAuth10RestGetEndpoint {

    private static final String ENDPOINT_URL = "https://api.twitter.com/1.1/statuses/home_timeline.json";
    private static final int MAX_ENTRIES = 50;

    HomeTimelineEndpoint(OAuth10Credentials credentials) {
        super(credentials);
    }

    public TwitterTimeline get() throws TwitterException {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("count", Integer.toString(MAX_ENTRIES)));
        params.add(new BasicNameValuePair("trim_user", "false"));
        params.add(new BasicNameValuePair("exclude_replies", "true"));
        params.add(new BasicNameValuePair("include_entities", "true"));

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
