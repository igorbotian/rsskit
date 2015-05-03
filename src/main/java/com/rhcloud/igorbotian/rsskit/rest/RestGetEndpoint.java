package com.rhcloud.igorbotian.rsskit.rest;

import com.rhcloud.igorbotian.rsskit.utils.URLUtils;
import org.apache.http.NameValuePair;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class RestGetEndpoint extends AbstractRestEndpoint {

    private static final Requestor REQUESTOR = new Requestor() {

        @Override
        public HttpURLConnection request(String endpoint, List<NameValuePair> params) throws IOException {
            URL url = URLUtils.makeURL(endpoint, params);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.connect();

            return connection;
        }
    };

    @Override
    protected Requestor requestor() {
        return REQUESTOR;
    }
}
