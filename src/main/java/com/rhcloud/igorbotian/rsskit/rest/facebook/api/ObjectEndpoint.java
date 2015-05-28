package com.rhcloud.igorbotian.rsskit.rest.facebook.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class ObjectEndpoint extends FacebookEndpoint {

    public ObjectEndpoint(FacebookAPI api) {
        super(api);
    }

    public JsonNode get(String id, String accessToken) throws FacebookException {
        Objects.requireNonNull(id);
        Objects.requireNonNull(accessToken);

        Set<NameValuePair> params = new HashSet<>();
        params.add(new BasicNameValuePair("access_token", accessToken));
        params.add(dateInUNIXTimeFormat());

        return makeRawJsonRequest(id, params);
    }
}
