package com.rhcloud.igorbotian.rsskit.rest.novayagazeta.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaArticle;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaAuthor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class NovayaGazetaArticleParser extends EntityParser<NovayaGazetaArticle> {

    private static final Logger LOGGER = LogManager.getLogger(NovayaGazetaArticleParser.class);
    private static final NovayaGazetaAuthorParser AUTHOR_PARSER = new NovayaGazetaAuthorParser();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public NovayaGazetaArticle parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);

        String id = getAttribute(json, "article_id").asText();
        Set<NovayaGazetaAuthor> authors = parseAuthors(getAttribute(json, "author_info"));
        String body = getAttribute(json, "article_body").asText();
        String sourceURL = getAttribute(json, "source_url").asText();
        String description = getAttribute(json, "description").asText();
        String subtitle = getAttribute(json, "subtitle").asText();
        String imageURL = getAttribute(json, "image_url").asText();
        String title = getAttribute(json, "title").asText();
        Date publishDate;

        try {
            publishDate = DATE_FORMAT.parse(getAttribute(json, "publish_date").asText());
        } catch (ParseException e) {
            LOGGER.warn("Failed to parse a publish date: " + getAttribute(json, "publish_date").asText(), e);
            publishDate = new Date();
        }

        return new NovayaGazetaArticle(id, authors, body, sourceURL, description, subtitle, publishDate, imageURL, title);
    }

    private Set<NovayaGazetaAuthor> parseAuthors(JsonNode json) throws RestParseException {
        assert json != null;

        Set<NovayaGazetaAuthor> authors = new HashSet<>(json.size());

        for(int i = 0; i < json.size(); i++) {
            authors.add(AUTHOR_PARSER.parse(json.get(i)));
        }

        return authors;
    }
}
