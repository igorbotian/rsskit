package com.rhcloud.igorbotian.rsskit.rest.meduza.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.meduza.MeduzaDocumentTitle;
import com.rhcloud.igorbotian.rsskit.rest.meduza.MeduzaIndex;

import java.util.*;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class MeduzaIndexParser extends EntityParser<MeduzaIndex> {

    private static final EntityParser<MeduzaDocumentTitle> DOCUMENT_PARSER = new MeduzaDocumentTitleParser();

    @Override
    public MeduzaIndex parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);

        Set<MeduzaDocumentTitle> titles = new LinkedHashSet<>();

        if (json.has("documents")) {
            JsonNode documents = json.get("documents");
            Iterator<Map.Entry<String, JsonNode>> it = documents.fields();

            while (it.hasNext()) {
                JsonNode document = it.next().getValue();
                titles.add(DOCUMENT_PARSER.parse(document));
            }
        }

        return new MeduzaIndex(titles);
    }
}
