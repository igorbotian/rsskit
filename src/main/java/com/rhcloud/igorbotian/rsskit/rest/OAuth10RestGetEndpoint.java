package com.rhcloud.igorbotian.rsskit.rest;

import com.rhcloud.igorbotian.rsskit.utils.URLUtils;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class OAuth10RestGetEndpoint extends RestGetEndpoint {

    protected final OAuthConsumer consumer;

    private Requestor requestor = new Requestor() {

        @Override
        public HttpResponse request(String endpoint, List<NameValuePair> params) throws IOException {
            Objects.requireNonNull(endpoint);
            Objects.requireNonNull(params);

            HttpGet request = new HttpGet(URLUtils.makeURL(endpoint, params).toString());

            try {
                consumer.sign(request);
            } catch (Exception e) {
                throw new IOException("Failed to sign a request", e);
            }

            return HTTP_CLIENT.execute(request);
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
