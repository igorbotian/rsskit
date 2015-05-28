package com.rhcloud.igorbotian.rsskit.rest.novayagazeta.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaArticleTitle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class NovayaGazetaArticleTitleParser extends EntityParser<NovayaGazetaArticleTitle> {

    private static final Logger LOGGER = LogManager.getLogger(NovayaGazetaArticleTitleParser.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public NovayaGazetaArticleTitle parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);

        String id = getAttribute(json, "article_id").asText();
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

        return new NovayaGazetaArticleTitle(id, sourceURL, description, subtitle, publishDate, imageURL, title);
    }
}
