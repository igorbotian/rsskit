package com.rhcloud.igorbotian.rsskit.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public abstract class AbstractRestEndpoint implements RestEndpoint {

    protected static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    protected static final HttpClient HTTP_CLIENT;
    private static final int CONNECTION_TIMEOUT = 30000;
    private static final int SOCKET_TIMEOUT = 60000;

    static {
        SSLContext context;

        try {
            context = SSLContext.getInstance("TLS");

            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to instantiate a no-op hostname verifier", e);
        }

        RequestConfig.Builder rcBuilder = RequestConfig.custom();

        rcBuilder.setConnectTimeout(CONNECTION_TIMEOUT);
        rcBuilder.setSocketTimeout(SOCKET_TIMEOUT);
        rcBuilder.setConnectionRequestTimeout(CONNECTION_TIMEOUT);

        String proxyHost = System.getProperty("http.proxyHost");
        String proxyPort = System.getProperty("http.proxyPort");

        if (StringUtils.isNotEmpty(proxyHost) && StringUtils.isNotEmpty(proxyPort)) {
            rcBuilder.setProxy(new HttpHost(proxyHost, Integer.parseInt(proxyPort)));
        }

        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultRequestConfig(rcBuilder.build());
        builder.setSSLHostnameVerifier(new NoopHostnameVerifier());
        builder.setSslcontext(context);

        HTTP_CLIENT = builder.build();
    }

    @Override
    public JsonNode makeRequest(String endpoint, Set<NameValuePair> params) throws IOException {
        Objects.requireNonNull(endpoint);
        Objects.requireNonNull(params);

        return makeRequest(endpoint, params, Collections.<NameValuePair>emptySet());
    }

    @Override
    public JsonNode makeRequest(String endpoint, Set<NameValuePair> params, Set<NameValuePair> headers) throws IOException {
        Objects.requireNonNull(endpoint);
        Objects.requireNonNull(params);
        Objects.requireNonNull(headers);

        byte[] content = makeRawRequest(endpoint, params, headers);
        return JSON_MAPPER.readTree(content);
    }

    protected byte[] makeRawRequest(String endpoint, Set<NameValuePair> params) throws IOException {
        Objects.requireNonNull(endpoint);
        Objects.requireNonNull(params);

        return makeRawRequest(endpoint, params, Collections.<NameValuePair>emptySet());
    }

    protected byte[] makeRawRequest(String endpoint, Set<NameValuePair> params, Set<NameValuePair> headers)
            throws IOException {

        Objects.requireNonNull(endpoint);
        Objects.requireNonNull(params);
        Objects.requireNonNull(headers);

        HttpResponse response = requestor().request(endpoint, params, headers);

        try {
            long contentLength = response.getEntity().getContentLength();
            return (contentLength < 0)
                    ? IOUtils.toByteArray(response.getEntity().getContent())
                    : IOUtils.toByteArray(response.getEntity().getContent(), contentLength);
        } finally {
            EntityUtils.consumeQuietly(response.getEntity());
        }
    }

    protected abstract Requestor requestor();

    protected static abstract class Requestor {

        public HttpResponse request(String endpoint, Set<NameValuePair> params) throws IOException {
            Objects.requireNonNull(endpoint);
            Objects.requireNonNull(params);

            return request(endpoint, params, Collections.<NameValuePair>emptySet());
        }

        public abstract HttpResponse request(String endpoint, Set<NameValuePair> params, Set<NameValuePair> headers)
                throws IOException;
    }
}
