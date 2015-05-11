package com.rhcloud.igorbotian.rsskit.db.instagram;

import com.rhcloud.igorbotian.rsskit.db.RsskitDAO;
import com.rhcloud.igorbotian.rsskit.db.RsskitDataSource;
import com.rhcloud.igorbotian.rsskit.db.RsskitEntityManager;
import com.rhcloud.igorbotian.rsskit.rest.instagram.InstagramException;

import java.sql.SQLException;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class InstagramEntityManagerImpl extends RsskitEntityManager implements InstagramEntityManager {

    private final RsskitDAO<InstagramToken> tokenDAO;
    private final RsskitDAO<InstagramSelfFeed> selfFeedDAO;

    public InstagramEntityManagerImpl(RsskitDataSource source) throws SQLException {
        super(source);

        this.tokenDAO = new RsskitDAO<>(dataSource, databaseType, InstagramToken.class);
        this.selfFeedDAO = new RsskitDAO<>(dataSource, databaseType, InstagramSelfFeed.class);
    }

    @Override
    public String registerAccessToken(String accessToken) throws InstagramException {
        Objects.requireNonNull(accessToken);

        try {
            String token = generateToken(accessToken);

            if (tokenDAO.exists(token)) {
                return token;
            }

            InstagramToken obj = new InstagramToken();
            obj.setAccessToken(accessToken);
            obj.setRsskitToken(token);

            tokenDAO.create(obj);

            return token;
        } catch (SQLException e) {
            throw new InstagramException("Failed to register access token by a specified user ID: " + accessToken, e);
        }
    }

    @Override
    public String getAccessToken(String token) throws InstagramException {
        Objects.requireNonNull(token);

        try {
            return tokenDAO.exists(token) ? tokenDAO.get(token).getAccessToken() : null;
        } catch (SQLException e) {
            throw new InstagramException("Failed to get Instagram access token by a specified user ID: " + token, e);
        }
    }

    @Override
    public void setSelfFeedMinID(String token, String minID) throws InstagramException {
        Objects.requireNonNull(token);
        Objects.requireNonNull(minID);

        try {
            if (selfFeedDAO.exists(token)) {
                InstagramSelfFeed feed = selfFeedDAO.get(token);
                feed.setMinID(minID);

                selfFeedDAO.update(feed);
            } else {
                InstagramSelfFeed feed = new InstagramSelfFeed();
                feed.setRsskitToken(token);
                feed.setMinID(minID);

                selfFeedDAO.create(feed);
            }
        } catch (SQLException e) {
            throw new InstagramException("Failed to store Instagram self feed MIN_ID parameter " +
                    "for a specified user ID: " + token, e);
        }
    }

    @Override
    public String getSelfFeedMinID(String token) throws InstagramException {
        Objects.requireNonNull(token);

        try {
            return selfFeedDAO.exists(token) ? selfFeedDAO.get(token).getMinID() : null;
        } catch (SQLException e) {
            throw new InstagramException("Failed to get Instagram self feed MIN_ID parameter " +
                    "by a specified user ID: " + token, e);
        }
    }
}
