package com.rhcloud.igorbotian.rsskit.rest.facebook.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class IncompleteFacebookNewsFeed {

    public static final EntityParser<IncompleteFacebookNewsFeed> PARSER = new IncompleteFacebookNewsFeedParser();

    public final List<IncompleteFacebookPost> posts;

    IncompleteFacebookNewsFeed(List<IncompleteFacebookPost> posts) {
        this.posts = Objects.requireNonNull(posts);
    }

    private static class IncompleteFacebookNewsFeedParser extends EntityParser<IncompleteFacebookNewsFeed> {

        @Override
        public IncompleteFacebookNewsFeed parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            List<IncompleteFacebookPost> items = new ArrayList<>(json.size());

            for(int i = 0; i < json.size(); i++) {
                items.add(IncompleteFacebookPost.PARSER.parse(json.get(i)));
            }

            return new IncompleteFacebookNewsFeed(items);
        }
    }
}
