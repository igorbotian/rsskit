package com.rhcloud.igorbotian.rsskit.rest.instagram;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.JSONUtils;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public final class InstagramResponse {

    private InstagramResponse() {
        //
    }

    public static <T> T parse(JsonNode json, EntityParser<T> parser) throws RestParseException, InstagramException {
        Objects.requireNonNull(json);
        Objects.requireNonNull(parser);

        checkNotError(json);
        return parser.parse(JSONUtils.getAttribute(json, "data"));
    }

    private static void checkNotError(JsonNode json) throws RestParseException, InstagramException {
        assert json != null;

        JsonNode meta = JSONUtils.getAttribute(json, "meta");
        int code = JSONUtils.getAttribute(meta, "code").asInt();

        if(code != 200) { // OK
            String errorType = JSONUtils.getAttribute(meta, "error_type").asText();
            String errorMessage = JSONUtils.getAttribute(meta, "error_message").asText();

            throw new InstagramException(String.format("%s: %s (%d)", errorType, errorMessage, code));
        }
    }
}
