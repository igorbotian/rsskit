package com.rhcloud.igorbotian.rsskit.rest.facebook;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
final class FacebookResponse {

    private FacebookResponse() {
        //
    }

    public static <T> T parse(JsonNode json, FacebookAPI api, String accessToken, FacebookEntityParser<T> entityParser)
            throws RestParseException, FacebookException {

        Objects.requireNonNull(json);
        Objects.requireNonNull(entityParser);
        Objects.requireNonNull(accessToken);

        throwExceptionIfError(json);

        if(!json.has("data")) {
            throw new RestParseException("No data returned by Facebook: " + json.toString());
        }

        return entityParser.parse(json.get("data"), api, accessToken);
    }

    public static void throwExceptionIfError(JsonNode json) throws FacebookException {
        Objects.requireNonNull(json);

        if(json.has("error")) {
            String type = json.has("type") ? json.get("type").asText() : "";
            int code = json.has("code") ? json.get("code").asInt() : 0;
            String message = json.has("message") ? json.get("message").asText() : "";

            throw new FacebookException(String.format("%s (%d): %s", type, code, message));
        }
    }
}
