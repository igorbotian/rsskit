package com.rhcloud.igorbotian.rsskit.rest;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public abstract class EntityParser<T> {

    public abstract T parse(JsonNode json) throws RestParseException;

    protected JsonNode getAttribute(JsonNode parent, String attr) throws RestParseException {
        Objects.requireNonNull(parent);
        Objects.requireNonNull(attr);

        return JSONUtils.getAttribute(parent, attr);
    }
}
