package com.rhcloud.igorbotian.rsskit.rest;

import com.rhcloud.igorbotian.rsskit.utils.URLUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class RestGetEndpoint extends AbstractRestEndpoint {

    private static final Requestor REQUESTOR = new Requestor() {

        @Override
        public HttpResponse request(String endpoint, List<NameValuePair> params) throws IOException {
            Objects.requireNonNull(endpoint);
            Objects.requireNonNull(params);

            HttpGet request = new HttpGet(URLUtils.makeURL(endpoint, params).toString());
            return HTTP_CLIENT.execute(request);
        }
    };

    @Override
    protected Requestor requestor() {
        return REQUESTOR;
    }
}
