package com.rhcloud.igorbotian.rsskit.mobilizer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class ReadabilityMobilizer implements Mobilizer {

    private static final String TOKEN = new String(
            Base64.getDecoder().decode("OWU0ODVkZWEwYmNkMWYzMGVmOWM3YzY5YmE2ZTY4ODk4MjhjNTZjYw=="),
            StandardCharsets.UTF_8
    );

    private static final String PROTOCOL = "https";
    private static final String HOST = "www.readability.com";
    private static final String PATH = "/api/content/v1/parser";
    private static final String URL_PARAM = "url";
    private static final String TOKEN_PARAM = "token";
    private static final String CONTENT = "content";

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String mobilize(URL url) throws IOException {
        Objects.requireNonNull(url);

        HttpURLConnection connection = (HttpURLConnection) (makeRequestURL(url)).openConnection();

        if (connection.getResponseCode() != 200) {
            throw new IOException(IOUtils.toString(connection.getInputStream()));
        }

        JsonNode json = mapper.readTree(connection.getInputStream());

        if (!json.has(CONTENT)) {
            throw new IOException(CONTENT + " field is not found in a response from Readability (but expected)");
        }

        return StringEscapeUtils.unescapeHtml4(json.get(CONTENT).asText());
    }

    private URL makeRequestURL(URL target) throws MalformedURLException {
        assert target != null;

        URIBuilder builder = new URIBuilder();
        builder.setScheme(PROTOCOL);
        builder.setHost(HOST);
        builder.setPath(PATH);
        builder.setParameter(TOKEN_PARAM, TOKEN);
        builder.setParameter(URL_PARAM, target.toString());

        try {
            return builder.build().toURL();
        } catch (URISyntaxException e) {
            throw new MalformedURLException(e.getMessage());
        }
    }
}
