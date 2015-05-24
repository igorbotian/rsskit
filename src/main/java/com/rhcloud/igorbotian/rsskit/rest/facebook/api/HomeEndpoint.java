package com.rhcloud.igorbotian.rsskit.rest.facebook.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.*;
import com.rhcloud.igorbotian.rsskit.rest.facebook.json.*;
import org.apache.commons.lang3.StringUtils;
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
    private static final int SIZE = 100;
    private static final FacebookLinkParser LINK_PARSER = new FacebookLinkParser();
    private static final FacebookOfferParser OFFER_PARSER = new FacebookOfferParser();
    private static final FacebookPhotoParser PHOTO_PARSER = new FacebookPhotoParser();
    private static final FacebookStatusParser STATUS_PARSER = new FacebookStatusParser();
    private static final FacebookVideoParser VIDEO_PARSER = new FacebookVideoParser();
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

    public FacebookNewsFeed getNewsFeed(String accessToken, Date since) throws FacebookException {
        Objects.requireNonNull(accessToken);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("access_token", accessToken));
        params.add(new BasicNameValuePair("fields", "id,type,from,message,caption,description,link,name,picture," +
                "object_id,story,created_time"));
        params.add(dateInUNIXTimeFormat());
        params.add(new BasicNameValuePair("limit", Integer.toString(SIZE)));

        List<IncompleteFacebookPost> posts = makeRequest(ENDPOINT, params, IncompleteFacebookNewsFeed.PARSER).posts;
        posts = filterPostsPublishedSinceGivenDate(posts, since);
        posts = orderPostsByCreatedTime(REPOST_IDENTIFIER.apply(posts));
        return makeNewsFeed(posts, accessToken);
    }

    private List<IncompleteFacebookPost> filterPostsPublishedSinceGivenDate(List<IncompleteFacebookPost> posts, Date since) {
        assert posts != null;
        assert since != null;

        List<IncompleteFacebookPost> newPosts = new ArrayList<>();

        for(IncompleteFacebookPost post : posts) {
            if(post.createdTime.after(since)) {
                newPosts.add(post);
            }
        }

        return newPosts;
    }

    private List<IncompleteFacebookPost> orderPostsByCreatedTime(List<IncompleteFacebookPost> incompletePosts) {
        assert incompletePosts != null;

        List<IncompleteFacebookPost> ordered = new ArrayList<>(incompletePosts);
        Collections.sort(ordered, BY_CREATED_TIME_DESC);
        return ordered;
    }

    private FacebookNewsFeed makeNewsFeed(List<IncompleteFacebookPost> incompletePosts, String accessToken) {
        assert incompletePosts != null;
        assert accessToken != null;

        List<FacebookNewsFeedItem> posts = new ArrayList<>(incompletePosts.size());

        for (IncompleteFacebookPost post : incompletePosts) {
            try {
                posts.add(makeNewsFeedItem(post, accessToken));
            } catch (FacebookException | RestParseException e) {
                LOGGER.trace("Failed to download post contents with a specified ID: " + post.id, e);

                try {
                    posts.add((post.source != null) ? makeRepost(post, accessToken) : makePost(post, accessToken));
                } catch (FacebookException ex) {
                    LOGGER.error("Failed to instantiate Facebook post/repost entity", ex);
                }
            }
        }

        return new FacebookNewsFeed(Collections.unmodifiableList(posts));
    }

    private FacebookRepost makeRepost(IncompleteFacebookPost post, String accessToken) throws FacebookException {
        assert post != null;
        assert accessToken != null;

        return new FacebookRepost(
                post.source.id,
                post.source.createdTime,
                post.source.from,
                makePost(post, accessToken)
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
                String name = (post.source != null && StringUtils.isNotEmpty(post.source.name))
                        ? post.source.name : post.name;
                return new FacebookLink(post.id, post.createdTime, post.from, post.caption, post.message,
                        post.description, post.link, name, post.picture);
            default:
                throw new FacebookException("Unexpected Facebook post type: " + post.type);
        }
    }

    private FacebookNewsFeedItem makeNewsFeedItem(IncompleteFacebookPost incompletePost, String accessToken)
            throws FacebookException, RestParseException {

        assert incompletePost != null;
        assert accessToken != null;

        JsonNode json = api.getObject(incompletePost.id, accessToken);
        FacebookPost post;

        switch (incompletePost.type) {
            case LINK:
                post = LINK_PARSER.parse(json);

                if (incompletePost.source != null && StringUtils.isNotEmpty(incompletePost.source.message)) {
                    FacebookLink link = (FacebookLink) post;
                    post = new FacebookLink(link.id, link.createdTime, link.from, link.caption,
                            link.message, link.description, link.link, incompletePost.source.name, link.picture);
                }
                break;
            case OFFER:
                post = OFFER_PARSER.parse(json);
                break;
            case PHOTO:
                String image = getPhotoImage(incompletePost.objectID, accessToken);
                FacebookPhoto photo = PHOTO_PARSER.parse(json);
                post = new FacebookPhoto(photo.id, photo.createdTime, photo.from, photo.caption,
                        photo.name, photo.link, photo.link, image, photo.picture);
                break;
            case STATUS:
                post = STATUS_PARSER.parse(json);
                break;
            case VIDEO:
                post = VIDEO_PARSER.parse(json);
                break;
            default:
                throw new FacebookException("Unrecognized Facebook post type: " + json.toString());
        }

        if (incompletePost.source != null) {
            return new FacebookRepost(
                    incompletePost.source.id, incompletePost.source.createdTime,
                    incompletePost.source.from, post
            );
        } else {
            return post;
        }
    }

    private String getPhotoImage(String objectID, String accessToken) {
        assert objectID != null;

        try {
            JsonNode json = api.getObject(objectID, accessToken);

            if(json.has("images")) {
                JsonNode images = json.get("images");

                if(images.size() > 0) {
                    JsonNode image = images.get(images.size() - 1);

                    if(image.has("source")) {
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
