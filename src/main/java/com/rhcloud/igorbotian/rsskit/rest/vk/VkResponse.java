package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkResponse {

    public static <T> T parse(JsonNode json, VkParser<T> entityParser) throws VkException {
        Objects.requireNonNull(json);
        Objects.requireNonNull(entityParser);

        if (json.has("error")) {
            processError(json.get("error"));
        }

        if (!json.has("response")) {
            throw new VkException("No data returned by VK: " + json.toString());
        }

        return entityParser.parse(json.get("response"));
    }

    private static void processError(JsonNode error) throws VkException {
        assert error != null;

        String message = "";

        if (error.has("message")) {
            message = error.get("message").asText();
        }

        if (error.has("code")) {
            message += String.format("(%d)", error.get("code").asInt());
        }

        throw new VkException(message);
    }
}
