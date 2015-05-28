package com.rhcloud.igorbotian.rsskit.rest.novayagazeta.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaArticlesTitles;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaArticleTitle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class NovayaGazetaArticlesTitlesParser extends EntityParser<NovayaGazetaArticlesTitles> {

    private static final NovayaGazetaArticleTitleParser ITEM_PARSER = new NovayaGazetaArticleTitleParser();

    @Override
    public NovayaGazetaArticlesTitles parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);

        JsonNode articles = getAttribute(json, "articles");
        List<NovayaGazetaArticleTitle> items = new ArrayList<>(articles.size());

        for(int i = 0; i < articles.size(); i++) {
            items.add(ITEM_PARSER.parse(articles.get(i)));
        }

        return new NovayaGazetaArticlesTitles(items);
    }
}
