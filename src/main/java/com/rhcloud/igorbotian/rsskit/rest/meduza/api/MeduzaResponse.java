package com.rhcloud.igorbotian.rsskit.rest.meduza.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
class MeduzaResponse {

    public static JsonNode parse(JsonNode response, String node) throws RestParseException {
        Objects.requireNonNull(response);
        Objects.requireNonNull(node);

        if(response.has(node)) {
            return response.get(node);
        }

        throw new RestParseException("Unexpected response was received from Meduza. No specified node found: " + node);
    }
}
