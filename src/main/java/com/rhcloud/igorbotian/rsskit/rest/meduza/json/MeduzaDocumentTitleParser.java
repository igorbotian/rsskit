package com.rhcloud.igorbotian.rsskit.rest.meduza.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.meduza.MeduzaDocumentTitle;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class MeduzaDocumentTitleParser extends EntityParser<MeduzaDocumentTitle> {

    @Override
    public MeduzaDocumentTitle parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);

        String url = getAttribute(json, "url").asText();
        Date publishedAt = new Date(getAttribute(json, "published_at").asLong() * 1000);

        return new MeduzaDocumentTitle(url, publishedAt);
    }
}
