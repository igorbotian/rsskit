package com.rhcloud.igorbotian.rsskit.rest;

import com.rhcloud.igorbotian.rsskit.utils.URLUtils;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import org.apache.http.NameValuePair;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class OAuth10RestGetEndpoint extends RestGetEndpoint {

    protected final OAuthConsumer consumer;

    private Requestor requestor = new Requestor() {

        @Override
        public HttpURLConnection request(String endpoint, List<NameValuePair> params) throws IOException {
            Objects.requireNonNull(endpoint);
            Objects.requireNonNull(params);

            HttpURLConnection conn = (HttpURLConnection) URLUtils.makeURL(endpoint, params).openConnection();
            conn.setDoInput(true);
            conn.setRequestMethod("GET");

            try {
                consumer.sign(conn);
            } catch (Exception e) {
                throw new IOException("Failed to sign a request", e);
            }

            conn.connect();

            return conn;
        }
    };

    public OAuth10RestGetEndpoint(OAuth10Credentials credentials) {
        Objects.requireNonNull(credentials);

        this.consumer = new DefaultOAuthConsumer(
                credentials.consumerKey,
                credentials.consumerSecret
        );

        if (credentials.areTokenAndSecretAvailable()) {
            this.consumer.setTokenWithSecret(credentials.accessToken, credentials.tokenSecret);
        }
    }

    @Override
    protected Requestor requestor() {
        return requestor;
    }
}
