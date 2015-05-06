package com.rhcloud.igorbotian.rsskit.rest.twitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterURL {

    private static final EntityParser<TwitterURL> PARSER = new TwitterURLsParser();

    public final String mediaURL;
    public final String expandedURL;
    public final int[] indices;

    public TwitterURL(String mediaURL, String expandedURL, int[] indices) {
        this.mediaURL = Objects.requireNonNull(mediaURL);
        this.expandedURL = Objects.requireNonNull(expandedURL);
        this.indices = Objects.requireNonNull(indices);
    }

    public static TwitterURL parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class TwitterURLsParser extends EntityParser<TwitterURL> {

        @Override
        public TwitterURL parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            String mediaURL = getAttribute(json, "url").asText();
            String expandedURL = getAttribute(json, "expanded_url").asText();

            JsonNode indicesNode = getAttribute(json, "indices");
            int[] indices = new int[indicesNode.size()];

            for(int i = 0 ; i < indicesNode.size(); i++) {
                indices[i] = indicesNode.get(i).asInt();
            }

            return new TwitterURL(mediaURL, expandedURL, indices);
        }
    }
}
