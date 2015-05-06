package com.rhcloud.igorbotian.rsskit.rest.instagram;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestGetEndpoint;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class MediaEndpoints extends RestGetEndpoint {

    private static final String SHORTCODE_ENDPOINT_URL = "https://api.instagram.com/v1/media/shortcode";
    private static final Pattern SHORTCODE_REGEX = Pattern.compile("https?://instagram.com/p/(\\w+)/?");

    public boolean isShortenURL(URL url) {
        Objects.requireNonNull(url);

        Matcher matcher = SHORTCODE_REGEX.matcher(url.toString());
        return matcher.matches();
    }

    public URL unshortURL(URL shortenURL, String accessToken) throws InstagramException {
        Objects.requireNonNull(shortenURL);
        Objects.requireNonNull(accessToken);

        try {
            String shortCode = parseShortCode(shortenURL);
            JsonNode response = makeRequest(
                    SHORTCODE_ENDPOINT_URL + "/" + shortCode,
                    Collections.<NameValuePair>singletonList(new BasicNameValuePair("access_token", accessToken))
            );

            return parseShortCode(response);
        } catch (IOException e) {
            throw new InstagramException("Failed to get information about an Instagram media object: "
                    + shortenURL.toString(), e);
        } catch (RestParseException e) {
            throw new InstagramException("Failed to parse Instagram response", e);
        }
    }

    private URL parseShortCode(JsonNode response) throws InstagramException, RestParseException {
        assert response != null;

        InstagramImages images = InstagramResponse.parse(response, new EntityParser<InstagramImages>() {

            @Override
            public InstagramImages parse(JsonNode json) throws RestParseException {
                Objects.requireNonNull(json);
                return InstagramImages.parse(json);
            }
        });

        try {
            return new URL(images.standardResolutionURL);
        } catch (MalformedURLException e) {
            throw new InstagramException("Instagram returned invalid URL of a given object " +
                    "specified by a short code", e);
        }
    }

    private String parseShortCode(URL url) throws InstagramException {
        assert url != null;

        Matcher matcher = SHORTCODE_REGEX.matcher(url.toString());

        if(matcher.matches() && matcher.groupCount() > 0) {
            return matcher.group(1);
        }

        throw new InstagramException("Specified URL contains no Instagram short code");
    }
}
