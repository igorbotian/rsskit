package com.rhcloud.igorbotian.rsskit.rest.meduza.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.meduza.MeduzaDocument;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class MeduzaDocumentParser extends EntityParser<MeduzaDocument> {

    @Override
    public MeduzaDocument parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);

        String url = getAttribute(json, "url").asText();
        String title = getAttribute(json, "title").asText();
        String documentType = getAttribute(json, "document_type").asText();
        Date publishedAt = new Date(getAttribute(json, "published_at").asLong() * 1000);
        JsonNode contentNode = json.has("content") ? json.get("content") : null;
        JsonNode bodyNode = (contentNode != null && !contentNode.isNull()) ? getAttribute(contentNode, "body") : null;
        String body = (bodyNode != null && !bodyNode.isNull()) ? bodyNode.asText() : "";
        String sourceURL = parseSourceAttr(json, "url");
        String sourceName = parseSourceAttr(json, "name");
        String description = parseNullNode(json, "description");
        String imageURL = json.has("image") ? json.get("image").get("small_url").asText() : "";
        String imageCaption = json.has("image") ? json.get("image").get("caption").asText() : "";

        return new MeduzaDocument(url, title, documentType, publishedAt, sourceName, sourceURL, body,
                description, imageURL, imageCaption);
    }

    private String parseNullNode(JsonNode json, String attr) {
        assert json != null;
        assert attr != null;

        if(json.has(attr)) {
            JsonNode node = json.get(attr);

            if(!node.isNull()) {
                return node.asText();
            }
        }

        return "";
    }

    private String parseSourceAttr(JsonNode json, String attr) {
        assert json != null;

        if(json.has("source")) {
            JsonNode source = json.get("source");

            if(source.has(attr)) {
                return source.get(attr).asText();
            }
        }

        return "";
    }
}
