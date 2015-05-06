package com.rhcloud.igorbotian.rsskit.rest.instagram;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class InstagramImages {

    private static final EntityParser<InstagramImages> PARSER = new InstagramImagesParser();

    public final String standardResolutionURL;

    public InstagramImages(String standardResolutionURL) {
        this.standardResolutionURL = Objects.requireNonNull(standardResolutionURL);
    }

    public static InstagramImages parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class InstagramImagesParser extends EntityParser<InstagramImages> {

        @Override
        public InstagramImages parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            JsonNode images = getAttribute(json, "images");
            JsonNode img = getAttribute(images, "standard_resolution");
            String url = getAttribute(img, "url").asText();

            return new InstagramImages(url);
        }
    }
}
