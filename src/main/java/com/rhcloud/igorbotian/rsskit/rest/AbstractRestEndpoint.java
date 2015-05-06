package com.rhcloud.igorbotian.rsskit.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public abstract class AbstractRestEndpoint implements RestEndpoint {

    protected static final HttpClient HTTP_CLIENT = HttpClientBuilder.create().build();
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @Override
    public JsonNode makeRequest(String endpoint, List<NameValuePair> params) throws IOException {
        Objects.requireNonNull(endpoint);
        Objects.requireNonNull(params);

        HttpResponse response = requestor().request(endpoint, params);

        long contentLength = response.getEntity().getContentLength();
        byte[] content = (contentLength < 0)
                ? IOUtils.toByteArray(response.getEntity().getContent())
                : IOUtils.toByteArray(response.getEntity().getContent(), contentLength);

        return JSON_MAPPER.readTree(content);
    }

    protected abstract Requestor requestor();

    protected interface Requestor {

        HttpResponse request(String endpoint, List<NameValuePair> params) throws IOException;
    }
}
