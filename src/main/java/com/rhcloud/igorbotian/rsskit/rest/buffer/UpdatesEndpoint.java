package com.rhcloud.igorbotian.rsskit.rest.buffer;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestGetEndpoint;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class UpdatesEndpoint extends RestGetEndpoint {

    private static final String ENDPOINT_URL = "https://api.bufferapp.com/1/profiles/%s/updates/pending.json";
    private static DateComparator DATE_COMPARATOR = new DateComparator();

    public TreeMap<Date, NameValuePair> getPending(String profileID, String accessToken, int count)
            throws BufferException {

        Objects.requireNonNull(profileID);
        Objects.requireNonNull(accessToken);

        if (count < 1) {
            throw new IllegalArgumentException("Count should have a positive value");
        }

        try {
            JsonNode response = makeRequest(profileID, accessToken, count);
            return parsePendingUpdates(response);
        } catch (IOException e) {
            throw new BufferException("Failed to make a request to the Buffer Updates endpoint");
        }
    }

    private JsonNode makeRequest(String profileID, String accessToken, int count) throws IOException, BufferException {
        assert profileID != null;
        assert accessToken != null;
        assert count > 0;

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("access_token", accessToken));
        params.add(new BasicNameValuePair("count", Integer.toString(count)));

        return makeRequest(new URL(String.format(ENDPOINT_URL, profileID)), params);
    }

    private TreeMap<Date, NameValuePair> parsePendingUpdates(JsonNode response) throws BufferException {
        assert response != null;

        TreeMap<Date, NameValuePair> updates = new TreeMap<>(DATE_COMPARATOR);

        if (!response.has("total")) {
            throw new BufferException("No number of pending updates returned by Buffer");
        }

        int total = response.get("total").asInt();

        if (total > 0) {
            if (!response.has("updates")) {
                throw new BufferException("No pending updates returned");
            }

            JsonNode pendingUpdates = response.get("updates");

            for (int i = 0; i < pendingUpdates.size(); i++) {
                JsonNode pendingUpdate = pendingUpdates.get(i);

                if (!pendingUpdate.has("created_at")) {
                    throw new BufferException("No pending update creation time returned by Buffer");
                }

                Date creationDate = new Date(pendingUpdate.get("created_at").asLong());
                updates.put(creationDate, parseTitleAndLink(pendingUpdate));
            }
        }

        return updates;
    }

    private NameValuePair parseTitleAndLink(JsonNode update) throws BufferException {
        assert update != null;

        if (!update.has("text")) {
            throw new BufferException("No pending update description time returned by Buffer");
        }

        String text = update.get("text").asText();
        String title = extractTitle(text);
        String link = null;

        if(update.has("media") && update.get("media").has("expanded_link")) {
            link = update.get("media").get("expanded_link").asText();
        }

        if(link == null) {
            link = extractLink(text);
        }

        return new BasicNameValuePair(title, link);
    }

    private String extractTitle(String text) {
        assert text != null;

        String title = text;

        int pos = text.indexOf("http");

        if (pos > 0) {
            title = text.substring(0, pos).trim();
        }

        return title;
    }

    private String extractLink(String text) {
        assert text != null;

        StringTokenizer tokenizer = new StringTokenizer(text, " ");

        while (tokenizer.hasMoreElements()) {
            String word = (String) tokenizer.nextElement();

            if (word.startsWith("http")) {
                return word;
            }
        }

        return null;
    }
}
