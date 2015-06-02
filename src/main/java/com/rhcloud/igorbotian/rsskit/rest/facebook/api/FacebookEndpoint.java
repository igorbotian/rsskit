package com.rhcloud.igorbotian.rsskit.rest.facebook.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.*;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
abstract class FacebookEndpoint {

    protected static final String GRAPH_API_URL = "https://graph.facebook.com/v%s";
    private static final NameValuePair DATE_FORMAT_PARAM = new BasicNameValuePair("date_format", "U");
    private static final RestGetEndpoint GET = new RestGetEndpoint();
    private static final RestPostEndpoint POST = new RestPostEndpoint();
    protected FacebookAPI api;

    public FacebookEndpoint(FacebookAPI api) {
        this.api = Objects.requireNonNull(api);
    }

    protected static NameValuePair dateInUNIXTimeFormat() {
        return DATE_FORMAT_PARAM;
    }

    protected JsonNode makeRawJsonGetRequest(String path, Set<NameValuePair> params) throws FacebookException {
        Objects.requireNonNull(path);
        Objects.requireNonNull(params);

        return makeRawJsonRequest(path, params, GET);
    }

    protected JsonNode makeRawJsonPostRequest(String path, Set<NameValuePair> params) throws FacebookException {
        Objects.requireNonNull(path);
        Objects.requireNonNull(params);

        return makeRawJsonRequest(path, params, POST);
    }

    private JsonNode makeRawJsonRequest(String path, Set<NameValuePair> params, AbstractRestEndpoint method)
            throws FacebookException {

        assert path != null;
        assert params != null;
        assert method != null;

        try {
            String endpoint = String.format(GRAPH_API_URL + "/" + path, api.version());
            JsonNode response = method.makeRequest(endpoint, params);
            FacebookResponse.throwExceptionIfError(response);
            return response;
        } catch (IOException e) {
            throw new FacebookException("Failed to make Facebook request", e);
        }
    }

    protected <T> T makeGetRequest(String path, Set<NameValuePair> params, EntityParser<T> parser) throws FacebookException {
        Objects.requireNonNull(path);
        Objects.requireNonNull(params);
        Objects.requireNonNull(parser);

        return makeRequest(path, params, parser, GET);
    }

    private <T> T makeRequest(String path, Set<NameValuePair> params, EntityParser<T> parser, RestGetEndpoint method)
            throws FacebookException {

        assert path != null;
        assert params != null;
        assert parser != null;
        assert method != null;

        try {
            JsonNode response = makeRawJsonRequest(path, params, method);
            return FacebookResponse.parse(response, parser);
        } catch (RestParseException e) {
            throw new FacebookException("Failed to parse Facebook response", e);
        }
    }
}
