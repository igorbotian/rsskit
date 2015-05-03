package com.rhcloud.igorbotian.rsskit.rest;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.NameValuePair;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public interface RestEndpoint {

    JsonNode makeRequest(URL endpoint, List<NameValuePair> params) throws IOException;
}
