package com.rhcloud.igorbotian.rsskit.rest.facebook.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.facebook.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
class HomeEndpoint extends FacebookEndpoint {

    private static final Logger LOGGER = LogManager.getLogger(HomeEndpoint.class);
    private static final String ENDPOINT = "me/home";
    private static final int SIZE = 50;
    private static final FacebookRepostIdentifier REPOST_IDENTIFIER = new FacebookRepostIdentifier();
    private static final Comparator<IncompleteFacebookPost> BY_CREATED_TIME_DESC
            = new Comparator<IncompleteFacebookPost>() {

        @Override
        public int compare(IncompleteFacebookPost first, IncompleteFacebookPost second) {
            return second.createdTime.compareTo(first.createdTime);
        }
    };

    public HomeEndpoint(FacebookAPI api) {
        super(api);
    }

    public List<FacebookFeedItem> getNewsFeed(String accessToken, Date since) throws FacebookException {
        Objects.requireNonNull(accessToken);

        Set<NameValuePair> params = new HashSet<>();
        params.add(new BasicNameValuePair("access_token", accessToken));
        params.add(new BasicNameValuePair("fields", "id,type,from,message,caption,description,link,name,picture," +
                "object_id,story,created_time"));
        params.add(dateInUNIXTimeFormat());
        params.add(new BasicNameValuePair("limit", Integer.toString(SIZE)));

        if (since != null) {
            params.add(new BasicNameValuePair("since", Long.toString(since.getTime() / 1000)));
        }

        List<IncompleteFacebookPost> posts = makeRequest(ENDPOINT, params, IncompleteFacebookNewsFeed.PARSER).posts;
        posts = orderPostsByCreatedTime(REPOST_IDENTIFIER.apply(posts));
        return makeNewsFeed(posts, accessToken);
    }

    private List<IncompleteFacebookPost> orderPostsByCreatedTime(List<IncompleteFacebookPost> incompletePosts) {
        assert incompletePosts != null;

        List<IncompleteFacebookPost> ordered = new ArrayList<>(incompletePosts);
        Collections.sort(ordered, BY_CREATED_TIME_DESC);
        return ordered;
    }

    private List<FacebookFeedItem> makeNewsFeed(List<IncompleteFacebookPost> incompletePosts, String accessToken) {
        assert incompletePosts != null;
        assert accessToken != null;

        List<FacebookFeedItem> posts = new ArrayList<>(incompletePosts.size());

        for (IncompleteFacebookPost post : incompletePosts) {
            try {
                posts.add(post.isRepost()
                        ? makeRepost(post, accessToken)
                        : new FacebookFeedItem(makePost(post, accessToken)));
            } catch (FacebookException ex) {
                LOGGER.error("Failed to instantiate Facebook post/repost entity", ex);
            }
        }

        return Collections.unmodifiableList(posts);
    }

    private FacebookFeedItem makeRepost(IncompleteFacebookPost post, String accessToken) throws FacebookException {
        assert post != null;
        assert accessToken != null;

        return new FacebookFeedItem(
                makePost(post, accessToken), makePost(post.source, accessToken)
        );
    }

    private FacebookPost makePost(IncompleteFacebookPost post, String accessToken) throws FacebookException {
        assert post != null;
        assert accessToken != null;

        switch (post.type) {
            case VIDEO:
                return new FacebookVideo(post.id, post.createdTime, post.from, post.caption, post.message,
                        post.name, post.description, post.picture, post.videoSource, "");
            case STATUS:
                return new FacebookStatus(post.id, post.createdTime, post.from, post.caption, post.message);
            case PHOTO:
                String image = getPhotoImage(post.objectID, accessToken);
                return new FacebookPhoto(post.id, post.createdTime, post.from, post.caption, post.message,
                        post.name, post.link, image, post.picture);
            case OFFER:
                return new FacebookOffer(post.id, post.createdTime, post.from, post.caption, post.message);
            case LINK:
                return new FacebookLink(post.id, post.createdTime, post.from, post.caption, post.message,
                        post.description, post.link, post.name, post.picture);
            default:
                throw new FacebookException("Unexpected Facebook post type: " + post.type);
        }
    }

    private String getPhotoImage(String objectID, String accessToken) {
        assert objectID != null;

        try {
            JsonNode json = api.getObject(objectID, accessToken);

            if (json.has("images")) {
                JsonNode images = json.get("images");

                if (images.size() > 0) {
                    JsonNode image = images.get(images.size() - 1);

                    if (image.has("source")) {
                        return image.get("source").asText();
                    }
                }
            }
        } catch (FacebookException e) {
            LOGGER.warn("Failed to download a photo image identified by ID = " + objectID, e);
            return "";
        }

        return "";
    }
}
