package com.rhcloud.igorbotian.rsskit.rest;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class JSONUtils {

    public static JsonNode getAttribute(JsonNode parent, String attr) throws RestParseException {
        Objects.requireNonNull(parent);
        Objects.requireNonNull(attr);

        if (!parent.has(attr)) {
            throw new RestParseException("Attribute is expected but not found: " + attr);
        }

        return parent.get(attr);
    }
}
