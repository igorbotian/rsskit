package com.rhcloud.igorbotian.rsskit.rest.meduza.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestGetEndpoint;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
class MeduzaEndpoint extends RestGetEndpoint {

    private static final String MEDUZA_API_URL = "https://meduza.io/api/v3";

    @Override
    public JsonNode makeRequest(String endpoint, Set<NameValuePair> params, Set<NameValuePair> otherParams) throws IOException {
        Objects.requireNonNull(endpoint);
        Objects.requireNonNull(params);
        Objects.requireNonNull(otherParams);

        Set<NameValuePair> headers = new HashSet<>();
        headers.add(new BasicNameValuePair("Accept", "*/*"));
        headers.add(new BasicNameValuePair("Accept-Encoding", "gzip, deflate"));
        headers.add(new BasicNameValuePair("User-Agent", "Meduza/161 (iPad4,4; iOS 8.1)"));
        headers.addAll(otherParams);

        return super.makeRequest(MEDUZA_API_URL + "/" + endpoint, params, headers);
    }
}
