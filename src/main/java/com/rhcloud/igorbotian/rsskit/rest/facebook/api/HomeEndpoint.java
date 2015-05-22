package com.rhcloud.igorbotian.rsskit.rest.facebook.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.*;
import com.rhcloud.igorbotian.rsskit.rest.facebook.json.*;
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
        params.add(new BasicNameValuePair("fields", "id,from,created_time,type,story,object_id"));
        params.add(dateInUNIXTimeFormat());
        params.add(new BasicNameValuePair("limit", Integer.toString(SIZE)));

        if (since != null) {
            params.add(since(since));
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

    private FacebookNewsFeed makeNewsFeed(List<IncompleteFacebookPost> incompletePosts, String accessToken) {
        assert incompletePosts != null;
        assert accessToken != null;

        List<FacebookNewsFeedItem> posts = new ArrayList<>(incompletePosts.size());

        for (IncompleteFacebookPost post : incompletePosts) {
            try {
                posts.add(makeNewsFeedItem(post, accessToken));
            } catch (FacebookException | RestParseException e) {
                LOGGER.trace("Failed to download post contents with a specified ID: " + post.id, e);
            }
        }

        return new FacebookNewsFeed(Collections.unmodifiableList(posts));
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
                break;
            case OFFER:
                post = OFFER_PARSER.parse(json);
                break;
            case PHOTO:
                post = PHOTO_PARSER.parse(json);
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
}
