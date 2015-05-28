package com.rhcloud.igorbotian.rsskit.rest;

import com.rhcloud.igorbotian.rsskit.utils.URLUtils;
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
public class RestGetEndpoint extends AbstractRestEndpoint {

    private static final Requestor REQUESTOR = new Requestor() {

        @Override
        public HttpResponse request(String endpoint, Set<NameValuePair> params, Set<NameValuePair> headers)
                throws IOException {

            Objects.requireNonNull(endpoint);
            Objects.requireNonNull(params);
            Objects.requireNonNull(headers);

            HttpGet request = new HttpGet(URLUtils.makeURL(endpoint, new ArrayList<>(params)).toString());

            for (NameValuePair header : headers) {
                request.setHeader(header.getName(), header.getValue());
            }

            return HTTP_CLIENT.execute(request);
        }
    };

    @Override
    protected Requestor requestor() {
        return REQUESTOR;
    }
}
