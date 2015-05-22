package com.rhcloud.igorbotian.rsskit.rest.facebook.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.json.FacebookProfileParser;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
class MeEndpoint extends FacebookEndpoint {

    private static final String ENDPOINT = "me";
    private static final FacebookProfileParser PARSER = new FacebookProfileParser();

    public MeEndpoint(FacebookAPI api) {
        super(api);
    }

    public String getID(String accessToken) throws FacebookException {
        Objects.requireNonNull(accessToken);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("access_token", accessToken));

        try {
            JsonNode response = makeRawJsonRequest(ENDPOINT, params);
            return PARSER.parse(response).id;
        } catch (RestParseException e) {
            throw new FacebookException("Failed to get user ID", e);
        }
    }
}
