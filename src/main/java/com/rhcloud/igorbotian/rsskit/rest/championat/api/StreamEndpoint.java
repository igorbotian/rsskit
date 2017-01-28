package com.rhcloud.igorbotian.rsskit.rest.championat.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.championat.ChampionatArticle;
import com.rhcloud.igorbotian.rsskit.rest.championat.ChampionatException;
import com.rhcloud.igorbotian.rsskit.rest.championat.ChampionatStream;
import com.rhcloud.igorbotian.rsskit.rest.championat.json.ChampionatArticleParser;
import com.rhcloud.igorbotian.rsskit.rest.championat.json.ChampionatStreamParser;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian
 */
class StreamEndpoint extends ChampionatEndpoint {

    private static final String ENDPOINT = "stream/";
    private static final EntityParser<ChampionatStream> STREAM_PARSER = new ChampionatStreamParser();
    private static final EntityParser<ChampionatArticle> ARTICLE_PARSER = new ChampionatArticleParser();

    public ChampionatStream getStream() throws ChampionatException {
        Set<NameValuePair> params = new HashSet<>();
        params.add(new BasicNameValuePair("sport", "football"));

        try {
            JsonNode response = makeRequest(ENDPOINT, params);
            return STREAM_PARSER.parse(response);
        } catch (RestParseException e) {
            throw new ChampionatException("Unexpected response received", e);
        } catch (IOException e) {
            throw new ChampionatException("Failed to get a news stream", e);
        }
    }

    public ChampionatArticle getArticle(String id) throws ChampionatException {
        Objects.requireNonNull(id);

        try {
            JsonNode response = makeRequest(ENDPOINT + id, Collections.<NameValuePair>emptySet());

            if (response.size() == 0) {
                throw new RestParseException("Unexpected response received from championat.com: " + response.toString());
            }

            return ARTICLE_PARSER.parse(response.get(0));
        } catch (RestParseException e) {
            throw new ChampionatException("Unexpected response received", e);
        } catch (IOException e) {
            throw new ChampionatException("Failed to get a specified article: " + id, e);
        }
    }
}
