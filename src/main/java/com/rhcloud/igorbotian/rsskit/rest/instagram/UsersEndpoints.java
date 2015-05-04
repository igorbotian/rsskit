package com.rhcloud.igorbotian.rsskit.rest.instagram;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestGetEndpoint;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class UsersEndpoints extends RestGetEndpoint {

    private static final String SELF_FEED_ENDPOINT_URL = "https://api.instagram.com/v1/users/self/feed";
    private static final int MAX_ENTRIES = 100;

    public InstagramFeed getSelfFeed(String accessToken) throws InstagramException {
        Objects.requireNonNull(accessToken);

        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("access_token", accessToken));
            params.add(new BasicNameValuePair("count", Integer.toString(MAX_ENTRIES)));

            JsonNode response = makeRequest(SELF_FEED_ENDPOINT_URL, params);
            return parseSelfFeed(response);
        } catch (IOException e) {
            throw new InstagramException("Failed to retrieve an Instagram self feed", e);
        }
    }

    private InstagramFeed parseSelfFeed(JsonNode response) throws InstagramException {
        assert response != null;

        JsonNode data = getAttribute(response, "data");
        List<InstagramPost> posts = new ArrayList<>();

        for(int i = 0; i < data.size(); i++) {
            posts.add(parsePost(data.get(i)));
        }

        return new InstagramFeed(posts);
    }

    private InstagramPost parsePost(JsonNode post) throws InstagramException {
        assert post != null;

        InstagramUser user = parseUser(getAttribute(post, "user"));
        String comment = post.has("caption") ? parseCaption(post.get("caption")) : "";
        String url = getAttribute(post, "link").asText();
        Date date = new Date(getAttribute(post, "created_time").asLong() * 1000);
        String thumbnailURL = parseThumbnail(getAttribute(post, "images"));

        return new InstagramPost(url, thumbnailURL, comment, date, user);
    }

    private String parseThumbnail(JsonNode images) throws InstagramException {
        assert images != null;

        JsonNode img = getAttribute(images, "standard_resolution");
        return getAttribute(img, "url").asText();
    }

    private String parseCaption(JsonNode caption) throws InstagramException {
        assert caption != null;
        return getAttribute(caption, "text").asText();
    }

    private InstagramUser parseUser(JsonNode user) throws InstagramException {
        assert user != null;

        String id = getAttribute(user, "id").asText();
        String username = getAttribute(user, "username").asText();
        String fullName = getAttribute(user, "full_name").asText();
        String profilePicture = getAttribute(user, "profile_picture").asText();

        return new InstagramUser(id, username, fullName, profilePicture);
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
