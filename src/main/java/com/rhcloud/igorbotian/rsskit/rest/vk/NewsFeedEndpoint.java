package com.rhcloud.igorbotian.rsskit.rest.vk;

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
class NewsFeedEndpoint extends RestGetEndpoint {

    private static final String ENDPOINT_URL = "https://api.vk.com/method/newsfeed.get";
    private static final int MAX_ENTRIES = 100;

    private final String apiVersion;

    public NewsFeedEndpoint(String apiVersion) {
        this.apiVersion = Objects.requireNonNull(apiVersion);
    }

    public VkFeed get(String accessToken) throws VkException {
        Objects.requireNonNull(accessToken);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("access_token", accessToken));
        params.add(new BasicNameValuePair("return_banned", "0"));
        params.add(new BasicNameValuePair("count", Integer.toString(MAX_ENTRIES)));
        params.add(new BasicNameValuePair("fields", "first_name,last_name,photo_50"));
        params.add(new BasicNameValuePair("filters", "post,photo,photo_tag,wall_photo"));
        params.add(new BasicNameValuePair("v", apiVersion));

        try {
            JsonNode response = makeRequest(ENDPOINT_URL, params);
            return parseNewsFeed(response);
        } catch (IOException e) {
            throw new VkException("Failed to retrieve the news feed of a specified VK user", e);
        }
    }

    private VkFeed parseNewsFeed(JsonNode json) throws VkException {
        assert json != null;

        return VkResponse.parse(json, new VkParser<VkFeed>() {

            @Override
            public VkFeed parse(JsonNode json) throws VkException {
                Objects.requireNonNull(json);
                return VkFeed.parse(json);
            }
        });
    }
}
