package com.rhcloud.igorbotian.rsskit.rest;

import com.rhcloud.igorbotian.rsskit.utils.URLUtils;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class OAuth10RestGetEndpoint extends RestGetEndpoint {

    protected final CommonsHttpOAuthConsumer consumer;

    private Requestor requestor = new Requestor() {

        @Override
        public HttpResponse request(String endpoint, Set<NameValuePair> params, Set<NameValuePair> headers)
                throws IOException {

            Objects.requireNonNull(endpoint);
            Objects.requireNonNull(params);
            Objects.requireNonNull(headers);

            HttpGet request = new HttpGet(URLUtils.makeURL(endpoint, new ArrayList<>(params)).toString());

            for(NameValuePair header : headers) {
                request.setHeader(header.getName(), header.getValue());
            }

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

        this.consumer = new CommonsHttpOAuthConsumer(
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
