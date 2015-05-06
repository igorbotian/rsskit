package com.rhcloud.igorbotian.rsskit.rest.twitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterEntities {

    private static final EntityParser<TwitterEntities> PARSER = new TwitterEntitiesParser();

    public final List<TwitterMedia> media;
    public final List<TwitterURL> urls;

    public TwitterEntities(List<TwitterMedia> media, List<TwitterURL> urls) {
        this.media = Collections.unmodifiableList(Objects.requireNonNull(media));
        this.urls = Collections.unmodifiableList(Objects.requireNonNull(urls));
    }

    public static TwitterEntities parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class TwitterEntitiesParser extends EntityParser<TwitterEntities> {

        @Override
        public TwitterEntities parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            List<TwitterMedia> media = json.has("media")
                    ? parseMedia(json.get("media"))
                    : Collections.<TwitterMedia>emptyList();

            List<TwitterURL> urls = json.has("urls")
                    ? parseURLs(json.get("urls"))
                    : Collections.<TwitterURL>emptyList();

            return new TwitterEntities(media, urls);
        }

        private List<TwitterMedia> parseMedia(JsonNode json) throws RestParseException {
            assert json != null;

            List<TwitterMedia> media = new ArrayList<>();

            for(int i = 0; i < json.size(); i++) {
                media.add(TwitterMedia.parse(json.get(i)));
            }

            return media;
        }

        private List<TwitterURL> parseURLs(JsonNode json) throws RestParseException {
            assert json != null;

            List<TwitterURL> urls = new ArrayList<>();

            for(int i = 0; i < json.size(); i++) {
                urls.add(TwitterURL.parse(json.get(i)));
            }

            return urls;
        }
    }
}
