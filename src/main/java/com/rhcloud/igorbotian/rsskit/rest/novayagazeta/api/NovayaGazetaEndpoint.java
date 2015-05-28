package com.rhcloud.igorbotian.rsskit.rest.novayagazeta.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestGetEndpoint;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
abstract class NovayaGazetaEndpoint extends RestGetEndpoint {

    public static final String API_URL = "http://api.novayagazeta.ru";

    @Override
    public JsonNode makeRequest(String endpoint, Set<NameValuePair> params) throws IOException {
        Objects.requireNonNull(endpoint);
        Objects.requireNonNull(params);

        Set<NameValuePair> headers = new HashSet<>();
        headers.add(new BasicNameValuePair("Accept", "*/*"));
        headers.add(new BasicNameValuePair("Accept-Encoding", "gzip, deflate"));
        headers.add(new BasicNameValuePair("Accept-Language", "en, ru, en-us"));
        headers.add(new BasicNameValuePair("User-Agent", "Novaya/1.1.0.164 CFNetwork/711.1.12 Darwin/14.0.0"));
        headers.add(new BasicNameValuePair("Connection", "keep-alive"));

        return super.makeRequest(API_URL + "/" + endpoint, params, headers);
    }
}
