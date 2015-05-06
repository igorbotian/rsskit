package com.rhcloud.igorbotian.rsskit.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public abstract class AbstractRestEndpoint implements RestEndpoint {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @Override
    public JsonNode makeRequest(String endpoint, List<NameValuePair> params) throws IOException {
        Objects.requireNonNull(endpoint);
        Objects.requireNonNull(params);

        HttpURLConnection response = requestor().request(endpoint, params);

        if (response.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Unable to make a request (" + response.getResponseCode() + ")");
        }

        long contentLength = response.getContentLength();
        byte[] content = (contentLength < 0)
                ? IOUtils.toByteArray(response.getInputStream())
                : IOUtils.toByteArray(response.getInputStream(), contentLength);

        return JSON_MAPPER.readTree(content);
    }

    protected abstract Requestor requestor();

    protected interface Requestor {

        HttpURLConnection request(String endpoint, List<NameValuePair> params) throws IOException;
    }
}
