package com.rhcloud.igorbotian.rsskit.rest;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.NameValuePair;

import java.io.IOException;
import java.util.Set;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public interface RestEndpoint {

    JsonNode makeRequest(String endpoint, Set<NameValuePair> params) throws IOException;
    JsonNode makeRequest(String endpoint, Set<NameValuePair> params, Set<NameValuePair> headers) throws IOException;
}
