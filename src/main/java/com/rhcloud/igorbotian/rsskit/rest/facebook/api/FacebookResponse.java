package com.rhcloud.igorbotian.rsskit.rest.facebook.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookException;

import java.util.Objects;

/**
 * @author Igor Botian
 */
final class FacebookResponse {

    private FacebookResponse() {
        //
    }

    public static <T> T parse(JsonNode json, EntityParser<T> entityParser)
            throws RestParseException, FacebookException {

        Objects.requireNonNull(json);
        Objects.requireNonNull(entityParser);

        throwExceptionIfError(json);

        if (!json.has("data")) {
            throw new RestParseException("No data returned by Facebook: " + json.toString());
        }

        return entityParser.parse(json.get("data"));
    }

    public static void throwExceptionIfError(JsonNode json) throws FacebookException {
        Objects.requireNonNull(json);

        if (json.has("error")) {
            JsonNode error = json.get("error");

            String type = error.has("type") ? error.get("type").asText() : "";
            int code = error.has("code") ? error.get("code").asInt() : 0;
            String message = error.has("message") ? error.get("message").asText() : "";

            throw new FacebookException(String.format("%s (%d): %s", type, code, message));
        }
    }
}
