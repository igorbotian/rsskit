package com.rhcloud.igorbotian.rsskit.rest.buffer;

import com.rhcloud.igorbotian.rsskit.utils.URLUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class AuthenticationEndpoint {

    private static final String ENDPOINT_URL = "https://bufferapp.com/oauth2/authorize";

    public URL authorize(String clientID, URL redirectURI) throws BufferException {
        Objects.requireNonNull(clientID);
        Objects.requireNonNull(redirectURI);

        try {
            return makeURL(clientID, redirectURI);
        } catch (IOException e) {
            throw new BufferException("Failed to make authorize request to the Buffer Authentication endpoint", e);
        }
    }

    private URL makeURL(String clientID, URL redirectURI) throws MalformedURLException {
        assert StringUtils.isNotEmpty(clientID);
        assert redirectURI != null;

        Map<String, String> params = new HashMap<>();
        params.put("client_id", clientID);
        params.put("redirect_uri", redirectURI.toString());
        params.put("response_type", "code");

        return URLUtils.makeURL(ENDPOINT_URL, params);
    }
}
