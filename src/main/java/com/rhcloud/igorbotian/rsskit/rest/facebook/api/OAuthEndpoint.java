package com.rhcloud.igorbotian.rsskit.rest.facebook.api;

import com.rhcloud.igorbotian.rsskit.rest.RestGetEndpoint;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookException;
import com.rhcloud.igorbotian.rsskit.utils.URLUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
class OAuthEndpoint extends RestGetEndpoint {

    private static final String AUTHORIZE_ENDPOINT_URL = "https://www.facebook.com/dialog/oauth";
    private static final String ACCESS_TOKEN_URL = "https://graph.facebook.com/oauth/access_token";

    public URL getAuthorizationURL(String clientID, Set<String> permissions, URL callbackURL)
            throws FacebookException {

        Objects.requireNonNull(clientID);
        Objects.requireNonNull(permissions);
        Objects.requireNonNull(callbackURL);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("client_id", clientID));
        params.add(new BasicNameValuePair("scope", StringUtils.join(permissions.iterator(), ',')));
        params.add(new BasicNameValuePair("redirect_uri", callbackURL.toString()));
        params.add(new BasicNameValuePair("response_type", "code"));

        try {
            return URLUtils.makeURL(AUTHORIZE_ENDPOINT_URL, params);
        } catch (MalformedURLException e) {
            throw new FacebookException("Failed to compose Facebook authorization URL", e);
        }
    }

    public AccessToken requestAccessToken(String clientID, String clientSecret, String code, URL callbackURL)
            throws FacebookException {

        Objects.requireNonNull(clientID);
        Objects.requireNonNull(clientSecret);
        Objects.requireNonNull(code);
        Objects.requireNonNull(callbackURL);

        Set<NameValuePair> params = new HashSet<>();
        params.add(new BasicNameValuePair("client_id", clientID));
        params.add(new BasicNameValuePair("client_secret", clientSecret));
        params.add(new BasicNameValuePair("redirect_uri", callbackURL.toString()));
        params.add(new BasicNameValuePair("code", code));

        try {
            String response = new String(makeRawRequest(ACCESS_TOKEN_URL, params));
            return parseAccessToken(response);
        } catch (IOException e) {
            throw new FacebookException("Failed to request Facebook access token", e);
        }
    }

    private AccessToken parseAccessToken(String queryString) throws FacebookException {
        assert queryString != null;

        String accessToken = null;
        Date expires = null;
        URIBuilder builder;

        try {
            builder = new URIBuilder("host?" + queryString);
        } catch (URISyntaxException e) {
            throw new FacebookException("Failed to parse Facebook response", e);
        }

        for (NameValuePair param : builder.getQueryParams()) {
            if ("access_token".equals(param.getName())) {
                accessToken = param.getValue();
            }

            if ("expires".equals(param.getName())) {
                expires = new Date(Long.parseLong(param.getValue()) * 1000 + System.currentTimeMillis());
            }
        }

        if (accessToken == null || expires == null) {
            try {
                FacebookResponse.throwExceptionIfError(JSON_MAPPER.readTree(queryString));
            } catch (IOException e) {
                throw new FacebookException("No access token and expired time was returned by Facebook");
            }
        }

        return new AccessToken(accessToken, expires);
    }
}
