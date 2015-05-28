package com.rhcloud.igorbotian.rsskit.rest.novayagazeta.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaAuthor;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class NovayaGazetaAuthorParser extends EntityParser<NovayaGazetaAuthor> {

    @Override
    public NovayaGazetaAuthor parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);

        String id = getAttribute(json, "author_id").asText();
        String position = getAttribute(json, "position").asText();
        String name = getAttribute(json, "name").asText();
        String imageURL = getAttribute(json, "image_url").asText();

        return new NovayaGazetaAuthor(id, position, name, imageURL);
    }
}
