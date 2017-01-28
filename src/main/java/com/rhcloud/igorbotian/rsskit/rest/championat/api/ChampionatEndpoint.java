package com.rhcloud.igorbotian.rsskit.rest.championat.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestGetEndpoint;
import com.rhcloud.igorbotian.rsskit.utils.Configuration;
import org.apache.commons.codec.binary.Base64;
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
abstract class ChampionatEndpoint extends RestGetEndpoint {

    private static final String API_URL = "http://api.championat.com/v2";
    private static final String CHAMPIONAT_API_LOGIN = Configuration.getProperty("CHAMPIONAT_API_LOGIN");
    private static final String CHAMPIONAT_API_PASSWORD = Configuration.getProperty("CHAMPIONAT_API_PASSWORD");

    @Override
    public JsonNode makeRequest(String endpoint, Set<NameValuePair> params) throws IOException {
        Objects.requireNonNull(endpoint);
        Objects.requireNonNull(params);

        return makeRequest(endpoint, params, Collections.<NameValuePair>emptySet());
    }

    @Override
    public JsonNode makeRequest(String endpoint, Set<NameValuePair> params, Set<NameValuePair> otherHeaders) throws IOException {
        Objects.requireNonNull(endpoint);
        Objects.requireNonNull(params);
        Objects.requireNonNull(otherHeaders);

        Set<NameValuePair> headers = new HashSet<>();
        headers.add(new BasicNameValuePair("Accept-Encoding", "gzip, deflate"));
        headers.add(new BasicNameValuePair("Accept", "*/*"));
        headers.add(new BasicNameValuePair("Accept-Language", "en-us"));
        headers.add(new BasicNameValuePair("Connection", "keep-alive"));
        headers.add(new BasicNameValuePair("User-Agent", "Championat/481 CFNetwork/711.1.12 Darwin/14.0.0"));
        headers.add(new BasicNameValuePair("Authorization", "Basic " + Base64.encodeBase64String(
                String.format("%s:%s", CHAMPIONAT_API_LOGIN, CHAMPIONAT_API_PASSWORD).getBytes())));
        headers.addAll(otherHeaders);

        return super.makeRequest(API_URL + "/" + endpoint, params, headers);
    }
}
