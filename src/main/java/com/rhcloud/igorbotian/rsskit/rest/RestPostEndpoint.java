package com.rhcloud.igorbotian.rsskit.rest;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class RestPostEndpoint extends AbstractRestEndpoint {

    private static final Requestor REQUESTOR = new Requestor() {

        @Override
        public HttpResponse request(String endpoint, List<NameValuePair> params) throws IOException {
            Objects.requireNonNull(endpoint);
            Objects.requireNonNull(params);

            HttpPost request = new HttpPost(endpoint);
            request.setEntity(new UrlEncodedFormEntity(params));

            return HTTP_CLIENT.execute(request);
        }
    };

    @Override
    protected Requestor requestor() {
        return REQUESTOR;
    }
}
