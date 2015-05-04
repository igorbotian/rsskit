package com.rhcloud.igorbotian.rsskit.rest.twitter;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.OAuth10Credentials;
import com.rhcloud.igorbotian.rsskit.rest.OAuth10RestGetEndpoint;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class HomeTimelineEndpoint extends OAuth10RestGetEndpoint {

    private static final String ENDPOINT_URL = "https://api.twitter.com/1.1/statuses/home_timeline.json";
    private static final int MAX_ENTRIES = 100;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");

    HomeTimelineEndpoint(OAuth10Credentials credentials) {
        super(credentials);
    }

    public TwitterTimeline get() throws TwitterException {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("count", Integer.toString(MAX_ENTRIES)));
        params.add(new BasicNameValuePair("trim_user", "false"));
        params.add(new BasicNameValuePair("exclude_replies", "true"));

        try {
            JsonNode response = makeRequest(ENDPOINT_URL, params);
            return parseTimeline(response);
        } catch (IOException e) {
            throw new TwitterException("Failed to get a Twitter home Timeline", e);
        }
    }

    private TwitterTimeline parseTimeline(JsonNode response) throws TwitterException {
        assert response != null;

        List<TwitterTweet> tweets = new ArrayList<>(response.size());

        for (int i = 0; i < response.size(); i++) {
            tweets.add(parseTweet(response.get(i)));
        }

        return new TwitterTimeline(tweets);
    }

    private TwitterTweet parseTweet(JsonNode json) throws TwitterException {
        assert json != null;

        String text = getAttribute(json, "text").asText();
        Date date;

        try {
            date = DATE_FORMAT.parse(getAttribute(json, "created_at").asText());
        } catch (ParseException e) {
            date = new Date();
        }

        String id = getAttribute(json, "id_str").asText();
        TwitterUser author = parseAuthor(getAttribute(json, "user"));
        List<String> attachments = parseAttachments(json);

        return new TwitterTweet(text, date, id, author, attachments);
    }

    private List<String> parseAttachments(JsonNode json) throws TwitterException {
        assert json != null;

        List<String> attachments = new ArrayList<>();

        if (json.has("entities") && json.get("entities").has("media")) {
            JsonNode media = json.get("entities").get("media");

            for (int i = 0; i < media.size(); i++) {
                attachments.add(getAttribute(media.get(i), "media_url").asText());
            }
        }

        return attachments;
    }

    private TwitterUser parseAuthor(JsonNode json) throws TwitterException {
        assert json != null;

        if (!json.has("screen_name")) {
            throw new TwitterException("No author's screen name returned by Twitter");
        }

        String name = getAttribute(json, "name").asText();
        String screenName = getAttribute(json, "screen_name").asText();
        String profileImageURL = getAttribute(json, "profile_image_url").asText();

        return new TwitterUser(name, screenName, profileImageURL);
    }

    private JsonNode getAttribute(JsonNode parent, String name) throws TwitterException {
        assert parent != null;
        assert name != null;

        if (!parent.has(name)) {
            throw new TwitterException("Expected attribute is expected but not found: " + name);
        }

        return parent.get(name);
    }
}
