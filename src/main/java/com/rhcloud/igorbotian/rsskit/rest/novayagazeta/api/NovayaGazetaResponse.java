package com.rhcloud.igorbotian.rsskit.rest.novayagazeta.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaException;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class NovayaGazetaResponse {

    private NovayaGazetaResponse() {
        //
    }

    public static JsonNode parse(JsonNode json) throws NovayaGazetaException {
        Objects.requireNonNull(json);

        if (json.has("error")) {
            JsonNode error = json.get("error");
            String description = error.has("description") ? error.get("description").asText() : "";
            int code = error.has("code") ? error.get("code").asInt() : 0;

            if(code != 200) { // OK
                throw new NovayaGazetaException(String.format("%s (%d)", description, code));
            }
        }

        return json;
    }
}
