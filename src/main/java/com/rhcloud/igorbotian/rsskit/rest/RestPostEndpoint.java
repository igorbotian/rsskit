package com.rhcloud.igorbotian.rsskit.rest;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class RestPostEndpoint extends AbstractRestEndpoint {

    private static final Requestor REQUESTOR = new Requestor() {

        @Override
        public HttpURLConnection request(URL endpoint, List<NameValuePair> params) throws IOException {
            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();

            new UrlEncodedFormEntity(params).writeTo(connection.getOutputStream());

            return connection;
        }
    };

    @Override
    protected Requestor requestor() {
        return REQUESTOR;
    }
}
