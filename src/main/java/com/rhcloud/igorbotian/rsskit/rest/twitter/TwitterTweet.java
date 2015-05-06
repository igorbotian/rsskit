package com.rhcloud.igorbotian.rsskit.rest.twitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterTweet {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
    private static final EntityParser<TwitterTweet> PARSER = new TwitterTweetParser();

    public final String id;
    public final String text;
    public final Date date;
    public final TwitterUser author;
    public final TwitterEntities entities;

    public TwitterTweet(String text, Date date, String id, TwitterUser author, TwitterEntities entities) {
        this.text = Objects.requireNonNull(text);
        this.date = Objects.requireNonNull(date);
        this.id = Objects.requireNonNull(id);
        this.author = Objects.requireNonNull(author);
        this.entities = Objects.requireNonNull(entities);
    }

    public static TwitterTweet parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class TwitterTweetParser extends EntityParser<TwitterTweet> {

        @Override
        public TwitterTweet parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);


            String text = getAttribute(json, "text").asText();
            Date date;

            try {
                date = DATE_FORMAT.parse(getAttribute(json, "created_at").asText());
            } catch (ParseException e) {
                date = new Date();
            }

            String id = getAttribute(json, "id_str").asText();
            TwitterUser author = TwitterUser.parse(getAttribute(json, "user"));
            TwitterEntities entities = TwitterEntities.parse(getAttribute(json, "entities"));

            return new TwitterTweet(text, date, id, author, entities);
        }
    }
}
