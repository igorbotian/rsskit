package com.rhcloud.igorbotian.rsskit.rest.instagram;

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
public class InstagramFeed {

    private static final EntityParser<InstagramFeed> PARSER = new InstagramFeedParser();

    public final List<InstagramPost> posts;

    public InstagramFeed(List<InstagramPost> posts) {
        this.posts = Collections.unmodifiableList(Objects.requireNonNull(posts));
    }

    public static InstagramFeed parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class InstagramFeedParser extends EntityParser<InstagramFeed> {

        @Override
        public InstagramFeed parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);
            List<InstagramPost> posts = new ArrayList<>();

            for (int i = 0; i < json.size(); i++) {
                JsonNode post = json.get(i);
                posts.add(InstagramPost.parse(post));
            }

            return new InstagramFeed(posts);
        }
    }
}
