package com.rhcloud.igorbotian.rsskit.rest;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class RestPostEndpoint extends AbstractRestEndpoint {

    private static final Requestor REQUESTOR = new Requestor() {

        @Override
        public HttpResponse request(String endpoint, Set<NameValuePair> params) throws IOException {
            Objects.requireNonNull(endpoint);
            Objects.requireNonNull(params);

            return request(endpoint, params, Collections.<NameValuePair>emptySet());
        }

        @Override
        public HttpResponse request(String endpoint, Set<NameValuePair> params, Set<NameValuePair> headers)
                throws IOException {

            Objects.requireNonNull(endpoint);
            Objects.requireNonNull(params);
            Objects.requireNonNull(headers);

            HttpPost request = new HttpPost(endpoint);

            for(NameValuePair header : headers) {
                request.setHeader(header.getName(), header.getValue());
            }

            request.setEntity(new UrlEncodedFormEntity(params));

            return HTTP_CLIENT.execute(request);
        }
    };

    @Override
    protected Requestor requestor() {
        return REQUESTOR;
    }
}
