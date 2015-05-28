package com.rhcloud.igorbotian.rsskit.rest.novayagazeta.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaException;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaArticlesTitles;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.json.NovayaGazetaArticlesTitlesParser;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class ArticlesEndpoint extends NovayaGazetaEndpoint {

    private static final EntityParser<NovayaGazetaArticlesTitles> PARSER = new NovayaGazetaArticlesTitlesParser();
    private final String endpoint;

    public ArticlesEndpoint(String endpoint) {
        this.endpoint = Objects.requireNonNull(endpoint);
    }

    public NovayaGazetaArticlesTitles getTitles(int limit, int offset) throws NovayaGazetaException {
        return getTitles(limit, offset, Collections.<NameValuePair>emptySet());
    }

    public NovayaGazetaArticlesTitles getTitles(int limit, int offset, Set<NameValuePair> otherParams)
            throws NovayaGazetaException {

        Objects.requireNonNull(otherParams);

        if (limit < 1) {
            throw new IllegalArgumentException("Limit should have a positive value: " + limit);
        }

        if (offset < 0) {
            throw new IllegalArgumentException("Offset should have a positive value: " + offset);
        }

        Set<NameValuePair> params = new HashSet<>();
        params.add(new BasicNameValuePair("limit", Integer.toString(limit)));
        params.add(new BasicNameValuePair("offset", Integer.toString(offset)));
        params.addAll(otherParams);

        try {
            JsonNode response = makeRequest(endpoint, params);
            response = NovayaGazetaResponse.parse(response);
            return PARSER.parse(response);
        } catch (IOException e) {
            throw new NovayaGazetaException("Failed to retrieve a list articles", e);
        } catch (RestParseException e) {
            throw new NovayaGazetaException("Unexpected response was sent by Novaya Gazeta", e);
        }
    }
}
