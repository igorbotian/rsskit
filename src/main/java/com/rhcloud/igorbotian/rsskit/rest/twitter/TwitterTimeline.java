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
public class TwitterTimeline {

    private static final EntityParser<TwitterTimeline> PARSER = new TwitterTimelineParser();

    public final List<TwitterTweet> tweets;

    public TwitterTimeline(List<TwitterTweet> tweets) {
        this.tweets = Collections.unmodifiableList(Objects.requireNonNull(tweets));
    }

    public static TwitterTimeline parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class TwitterTimelineParser extends EntityParser<TwitterTimeline> {

        @Override
        public TwitterTimeline parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            List<TwitterTweet> tweets = new ArrayList<>(json.size());

            for (int i = 0; i < json.size(); i++) {
                tweets.add(TwitterTweet.parse(json.get(i)));
            }

            return new TwitterTimeline(tweets);
        }
    }
}
