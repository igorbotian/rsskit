package com.rhcloud.igorbotian.rsskit.rest.facebook.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestGetEndpoint;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
class FacebookEndpoint extends RestGetEndpoint {

    private static final String GRAPH_API_URL = "https://graph.facebook.com/v%s";
    private static final NameValuePair DATE_FORMAT_PARAM = new BasicNameValuePair("date_format", "U");
    protected FacebookAPI api;

    public FacebookEndpoint(FacebookAPI api) {
        this.api = Objects.requireNonNull(api);
    }

    protected static NameValuePair dateInUNIXTimeFormat() {
        return DATE_FORMAT_PARAM;
    }

    protected JsonNode makeRawJsonRequest(String path, List<NameValuePair> params) throws FacebookException {
        Objects.requireNonNull(path);
        Objects.requireNonNull(params);

        try {
            JsonNode response = makeRequest(String.format(GRAPH_API_URL + "/" + path, api.version()), params);
            FacebookResponse.throwExceptionIfError(response);
            return response;
        } catch (IOException e) {
            throw new FacebookException("Failed to make Facebook request", e);
        }
    }

    protected <T> T makeRequest(String path, List<NameValuePair> params, EntityParser<T> parser) throws FacebookException {
        Objects.requireNonNull(path);
        Objects.requireNonNull(params);
        Objects.requireNonNull(parser);

        try {
            JsonNode response = makeRawJsonRequest(path, params);
            return FacebookResponse.parse(response, parser);
        } catch (RestParseException e) {
            throw new FacebookException("Failed to parse Facebook response", e);
        }
    }
}
