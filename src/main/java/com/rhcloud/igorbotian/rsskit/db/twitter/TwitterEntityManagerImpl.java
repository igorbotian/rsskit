package com.rhcloud.igorbotian.rsskit.db.twitter;

import com.rhcloud.igorbotian.rsskit.db.RsskitDAO;
import com.rhcloud.igorbotian.rsskit.db.RsskitDataSource;
import com.rhcloud.igorbotian.rsskit.db.RsskitEntityManager;
import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterAccessToken;
import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterException;

import java.sql.SQLException;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterEntityManagerImpl extends RsskitEntityManager implements TwitterEntityManager {

    private final RsskitDAO<TwitterToken> tokenDAO;
    private final RsskitDAO<TwitterHomeTimeline> homeTimelineDAO;

    public TwitterEntityManagerImpl(RsskitDataSource source) throws SQLException {
        super(source);

        this.tokenDAO = new RsskitDAO<>(dataSource, databaseType, TwitterToken.class);
        this.homeTimelineDAO = new RsskitDAO<>(dataSource, databaseType, TwitterHomeTimeline.class);
    }

    public String registerAccessToken(TwitterAccessToken accessToken) throws TwitterException {
        Objects.requireNonNull(accessToken);

        try {
            String token = generateToken(accessToken.tokenSecret + "" + accessToken.accessToken);

            if (tokenDAO.exists(token)) {
                return token;
            }

            TwitterToken obj = new TwitterToken();
            obj.setTokenSecret(accessToken.tokenSecret);
            obj.setAccessToken(accessToken.accessToken);
            obj.setRsskitToken(token);

            tokenDAO.create(obj);

            return token;
        } catch (SQLException e) {
            throw new TwitterException("Failed to register access token by a specified user ID: " + accessToken, e);
        }
    }

    public TwitterAccessToken getAccessToken(String token) throws TwitterException {
        Objects.requireNonNull(token);

        try {
            if (!tokenDAO.exists(token)) {
                return null;
            }

            TwitterToken accessToken = tokenDAO.get(token);
            return new TwitterAccessToken(accessToken.getAccessToken(), accessToken.getTokenSecret());
        } catch (SQLException e) {
            throw new TwitterException("Failed to get Twitter access token by a specified user ID: " + token, e);
        }
    }

    public void setHomeTimelineSinceID(String token, String sinceID) throws TwitterException {
        Objects.requireNonNull(token);
        Objects.requireNonNull(sinceID);

        try {
            if (homeTimelineDAO.exists(token)) {
                TwitterHomeTimeline feed = homeTimelineDAO.get(token);
                feed.setSinceID(sinceID);

                homeTimelineDAO.update(feed);
            } else {
                TwitterHomeTimeline feed = new TwitterHomeTimeline();
                feed.setRsskitToken(token);
                feed.setSinceID(sinceID);

                homeTimelineDAO.create(feed);
            }
        } catch (SQLException e) {
            throw new TwitterException("Failed to store Twitter home timeline SINCE_ID parameter " +
                    "for a specified user ID: " + token, e);
        }
    }

    public String getHomeTimelineSinceID(String token) throws TwitterException {
        Objects.requireNonNull(token);

        try {
            return homeTimelineDAO.exists(token) ? homeTimelineDAO.get(token).getSinceID() : null;
        } catch (SQLException e) {
            throw new TwitterException("Failed to get Twitter home timeline SINCE_ID parameter " +
                    "by a specified user ID: " + token, e);
        }
    }
}
