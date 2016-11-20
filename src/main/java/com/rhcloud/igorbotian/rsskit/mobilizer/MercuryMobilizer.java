package com.rhcloud.igorbotian.rsskit.mobilizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rhcloud.igorbotian.rsskit.utils.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class MercuryMobilizer implements Mobilizer {

    private static final String WEB_PARSER_API_KEY = "MERCURY_WEB_PARSER_API_KEY";
    private static final String API_KEY = Configuration.getProperty(WEB_PARSER_API_KEY);

    private static final String PROTOCOL = "https";
    private static final String HOST = "mercury.postlight.com";
    private static final String PATH = "/parser";
    private static final String URL_PARAM = "url";
    private static final String API_KEY_PARAM = "x-api-key";
    private static final String CONTENT = "content";

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final CloseableHttpClient HTTP_CLIENT = HttpClients.createMinimal();

    @Override
    public String mobilize(URL url) throws IOException {
        Objects.requireNonNull(url);

        byte[] content = downloadContent(makeRequestURL(url));
        JsonNode json = mapper.readTree(content);

        if (!json.has(CONTENT)) {
            throw new IOException(CONTENT + " field is not found in a response from Mercury (but expected)");
        }

        return StringEscapeUtils.unescapeHtml4(json.get(CONTENT).asText());
    }

    private URL makeRequestURL(URL target) throws MalformedURLException {
        assert target != null;

        URIBuilder builder = new URIBuilder();
        builder.setScheme(PROTOCOL);
        builder.setHost(HOST);
        builder.setPath(PATH);
        builder.setParameter(URL_PARAM, target.toString());

        try {
            return builder.build().toURL();
        } catch (URISyntaxException e) {
            throw new MalformedURLException(e.getMessage());
        }
    }

    private byte[] downloadContent(URL url) throws IOException {
        assert url != null;

        try {
            HttpGet request = new HttpGet(url.toURI());
            request.addHeader(API_KEY_PARAM, API_KEY);
            CloseableHttpResponse response = HTTP_CLIENT.execute(request);

            try {
                long contentLength = response.getEntity().getContentLength();
                return (contentLength < 0)
                        ? IOUtils.toByteArray(response.getEntity().getContent())
                        : IOUtils.toByteArray(response.getEntity().getContent(), contentLength);
            } finally {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        } catch (URISyntaxException e) {
            throw new MalformedURLException(e.getMessage());
        }
    }
}
