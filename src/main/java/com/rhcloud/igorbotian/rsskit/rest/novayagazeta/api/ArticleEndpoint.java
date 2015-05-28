package com.rhcloud.igorbotian.rsskit.rest.novayagazeta.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaArticle;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaException;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.json.NovayaGazetaArticleParser;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class ArticleEndpoint extends NovayaGazetaEndpoint {

    private static final String ENDPOINT = "article/";
    private static final EntityParser<NovayaGazetaArticle> PARSER = new NovayaGazetaArticleParser();

    public NovayaGazetaArticle get(String id) throws NovayaGazetaException {
        Objects.requireNonNull(id);

        try {
            JsonNode response = makeRequest(ENDPOINT, Collections.<NameValuePair>singleton(
                    new BasicNameValuePair("article_id", id)));
            response = NovayaGazetaResponse.parse(response);
            return PARSER.parse(response);
        } catch (IOException e) {
            throw new NovayaGazetaException("Failed to retrieve a specified article: " + id, e);
        } catch (RestParseException e) {
            throw new NovayaGazetaException("Unexpected response was sent by Novaya Gazeta", e);
        }
    }
}
