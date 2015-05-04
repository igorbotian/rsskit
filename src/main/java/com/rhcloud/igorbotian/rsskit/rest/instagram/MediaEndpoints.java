package com.rhcloud.igorbotian.rsskit.rest.instagram;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestGetEndpoint;
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
            return parseMediaObject(response);
        } catch (IOException e) {
            throw new InstagramException("Failed to get information about an Instagram media object: "
                    + shortenURL.toString(), e);
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

    private URL parseMediaObject(JsonNode response) throws InstagramException {
        assert response != null;

        JsonNode data = getAttribute(response, "data");
        JsonNode images = getAttribute(data, "images");
        JsonNode img = getAttribute(images, "standard_resolution");

        try {
            return new URL(getAttribute(img, "url").asText());
        } catch (MalformedURLException e) {
            throw new InstagramException("No media object URL found", e);
        }
    }

    private JsonNode getAttribute(JsonNode parent, String attr) throws InstagramException {
        assert parent != null;
        assert attr != null;

        if(!parent.has(attr)) {
            throw new InstagramException("Attribute is expected but not found: " + attr);
        }

        return parent.get(attr);
    }
}
