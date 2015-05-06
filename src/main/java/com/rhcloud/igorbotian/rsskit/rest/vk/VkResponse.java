package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public final class VkResponse {

    private VkResponse() {
        //
    }

    public static <T> T parse(JsonNode json, EntityParser<T> entityParser) throws RestParseException, VkException {
        Objects.requireNonNull(json);
        Objects.requireNonNull(entityParser);

        if (json.has("error")) {
            processError(json.get("error"));
        }

        if (!json.has("response")) {
            throw new RestParseException("No data returned by VK: " + json.toString());
        }

        return entityParser.parse(json.get("response"));
    }

    private static void processError(JsonNode error) throws VkException {
        assert error != null;

        String message = "";

        if (error.has("error_msg")) {
            message = error.get("error_msg").asText();
        }

        if (error.has("error_code")) {
            message += String.format(" (%d)", error.get("error_code").asInt());
        }

        throw new VkException(message);
    }
}
