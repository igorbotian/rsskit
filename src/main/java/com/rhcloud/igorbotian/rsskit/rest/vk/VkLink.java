package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkLink {

    private static final EntityParser<VkLink> PARSER = new VkLinkParser();

    public final String url;
    public final String title;
    public final String description;
    public final String imageSrc;

    public VkLink(String url, String title, String description, String imageSrc) {
        this.url = Objects.requireNonNull(url);
        this.title = Objects.requireNonNull(title);
        this.description = Objects.requireNonNull(description);
        this.imageSrc = imageSrc;
    }

    public static VkLink parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class VkLinkParser extends EntityParser<VkLink> {

        @Override
        public VkLink parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            String url = getAttribute(json, "url").asText();
            String title = getAttribute(json, "title").asText();
            String description = getAttribute(json, "description").asText();
            String imageSrc = null;

            if(json.has("image_src")) {
                imageSrc = json.get("image_src").asText();
            }

            return new VkLink(url, title, description, imageSrc);
        }
    }
}
