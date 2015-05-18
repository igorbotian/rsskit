package com.rhcloud.igorbotian.rsskit.rest.facebook;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.JSONUtils;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public abstract class FacebookEntityParser<T> {

    public abstract T parse(JsonNode json, FacebookAPI api, String accessToken) throws RestParseException;

    protected JsonNode getAttribute(JsonNode parent, String attr) throws RestParseException {
        Objects.requireNonNull(parent);
        Objects.requireNonNull(attr);

        return JSONUtils.getAttribute(parent, attr);
    }
}
