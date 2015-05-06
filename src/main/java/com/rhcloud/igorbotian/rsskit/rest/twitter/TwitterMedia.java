package com.rhcloud.igorbotian.rsskit.rest.twitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterMedia {

    private static final EntityParser<TwitterMedia> PARSER = new TwitterMediaParser();

    public final String url;
    public final String mediaURL;
    public final String expandedURL;
    public final String type;
    public final int[] indices;

    public TwitterMedia(String url, String mediaURL, String expandedURL, String type, int[] indices) {
        this.url = Objects.requireNonNull(url);
        this.mediaURL = Objects.requireNonNull(mediaURL);
        this.expandedURL = Objects.requireNonNull(expandedURL);
        this.type = Objects.requireNonNull(type);
        this.indices = Objects.requireNonNull(indices);
    }

    public static TwitterMedia parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class TwitterMediaParser extends EntityParser<TwitterMedia> {

        @Override
        public TwitterMedia parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            String url = getAttribute(json, "url").asText();
            String mediaURL = getAttribute(json, "media_url").asText();
            String expandedURL = getAttribute(json, "expanded_url").asText();
            String type = getAttribute(json, "type").asText();

            JsonNode indicesNode = getAttribute(json, "indices");
            int[] indices = new int[indicesNode.size()];

            for(int i = 0 ; i < indicesNode.size(); i++) {
                indices[i] = indicesNode.get(i).asInt();
            }

            return new TwitterMedia(url, mediaURL, expandedURL, type, indices);
        }
    }
}
