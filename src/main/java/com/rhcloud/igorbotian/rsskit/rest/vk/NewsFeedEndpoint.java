package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestGetEndpoint;
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
class NewsFeedEndpoint extends RestGetEndpoint {

    private static final String ENDPOINT_URL = "https://api.vk.com/method/newsfeed.get";
    private static final int MAX_ENTRIES = 100;

    private final String apiVersion;

    public NewsFeedEndpoint(String apiVersion) {
        this.apiVersion = Objects.requireNonNull(apiVersion);
    }

    public VkFeed get(String accessToken, Long startTime) throws VkException {
        Objects.requireNonNull(accessToken);

        if (startTime != null && startTime < 0) {
            throw new VkException("Start time parameter should have a positive value" + startTime);
        }

        Set<NameValuePair> params = new HashSet<>();
        params.add(new BasicNameValuePair("access_token", accessToken));
        params.add(new BasicNameValuePair("return_banned", "0"));
        params.add(new BasicNameValuePair("count", Integer.toString(MAX_ENTRIES)));
        params.add(new BasicNameValuePair("fields", "first_name,last_name,screen_name,photo_50"));
        params.add(new BasicNameValuePair("filters", "post,photo,photo_tag,wall_photo"));
        params.add(new BasicNameValuePair("v", apiVersion));

        if (startTime != null) {
            params.add(new BasicNameValuePair("start_time", Long.toString(startTime / 1000)));
        }

        try {
            JsonNode response = makeRequest(ENDPOINT_URL, params);
            return parseNewsFeed(response);
        } catch (IOException e) {
            throw new VkException("Failed to retrieve the news feed of a specified VK user", e);
        } catch (RestParseException e) {
            throw new VkException("Failed to parse VK response", e);
        }
    }

    private VkFeed parseNewsFeed(JsonNode json) throws RestParseException, VkException {
        assert json != null;

        return VkResponse.parse(json, new EntityParser<VkFeed>() {

            @Override
            public VkFeed parse(JsonNode json) throws RestParseException {
                Objects.requireNonNull(json);
                return VkFeed.parse(json);
            }
        });
    }
}
