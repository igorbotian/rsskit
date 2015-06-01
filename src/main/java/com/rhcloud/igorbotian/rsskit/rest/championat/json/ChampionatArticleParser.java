package com.rhcloud.igorbotian.rsskit.rest.championat.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.championat.ChampionatArticle;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class ChampionatArticleParser extends EntityParser<ChampionatArticle> {

    @Override
    public ChampionatArticle parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);

        String id = getAttribute(json, "_id").asText();
        Date pubDate = new Date(getAttribute(json, "pub_date").asLong() * 1000);
        String content = parseContent(getAttribute(json, "content"));
        String externalID = getAttribute(json, "id").asText();
        String directLink = getAttribute(json, "direct_link").asText();
        String title = getAttribute(json, "title").asText();
        String type = getAttribute(json, "type").asText();
        String sport = getAttribute(json, "sport").asText();
        JsonNode image = json.has("image") ? json.get("image") : null;
        String imageURL = (image != null) ? image.get("url").asText() : "";
        String imageCaption = (image != null) ? (image.has("caption") ? image.get("caption").asText() : "") : "";
        boolean breaking = "2".equals(getAttribute(getAttribute(getAttribute(json, "visible"), "ru"), "on_sport").asText());

        return new ChampionatArticle(id, pubDate, content, directLink, externalID, title, type,
                sport, imageURL, imageCaption, breaking);
    }

    private String parseContent(JsonNode json) {
        assert json != null;

        StringBuilder content = new StringBuilder();

        for (int i = 0; i < json.size(); i++) {
            JsonNode node = json.get(i);

            if (node.has("text")) {
                if (content.length() > 0) {
                    content.append("<br/>");
                }

                content.append(node.get("text").asText());
            } else if (node.has("paragraph")) {
                content.append("<br/>");
            }
        }

        return removeTrailingBreaks(content.toString());
    }

    private String removeTrailingBreaks(String content) {
        assert content != null;

        String result = content;
        int length;

        while ((length = lengthOfTrailingBreak(result)) > 0) {
            result = result.substring(0, result.length() - length);
        }

        return result;
    }

    private int lengthOfTrailingBreak(String content) {
        assert content != null;

        if (content.endsWith("<br/>")) {
            return "<br/>".length();
        } else if (content.endsWith("<br>")) {
            return "<br>".length();
        } else if (content.endsWith("&lt;br/&gt;")) {
            return "&lt;br/&gt;".length();
        }
        return -1;
    }
}
