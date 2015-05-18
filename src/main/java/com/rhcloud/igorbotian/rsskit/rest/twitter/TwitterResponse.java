package com.rhcloud.igorbotian.rsskit.rest.twitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.JSONUtils;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
final class TwitterResponse {

    private TwitterResponse() {
        //
    }

    public static <T> T parse(JsonNode json, EntityParser<T> parser) throws TwitterException, RestParseException {
        Objects.requireNonNull(json);
        Objects.requireNonNull(parser);

        checkNotError(json);
        return parser.parse(json);
    }

    private static void checkNotError(JsonNode json) throws TwitterException, RestParseException {
        assert json != null;

        if(json.has("errors")) {
            JsonNode errors = json.get("errors");
            JsonNode firstError = errors.get(0);
            String message = JSONUtils.getAttribute(firstError, "message").asText();
            int code = JSONUtils.getAttribute(firstError, "code").asInt();

            throw new TwitterException(String.format("%s (%d)", message, code));
        }
    }
}
