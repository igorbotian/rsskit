package com.rhcloud.igorbotian.rsskit.db.vk;

import com.rhcloud.igorbotian.rsskit.db.RsskitDAO;
import com.rhcloud.igorbotian.rsskit.db.RsskitDataSource;
import com.rhcloud.igorbotian.rsskit.db.RsskitEntityManager;
import com.rhcloud.igorbotian.rsskit.rest.vk.VkException;

import java.sql.SQLException;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkEntityManagerImpl extends RsskitEntityManager implements VkEntityManager {

    private final RsskitDAO<VkToken> tokenDAO;
    private final RsskitDAO<VkNewsFeed> newsFeedDAO;

    public VkEntityManagerImpl(RsskitDataSource source) throws SQLException {
        super(source);

        this.tokenDAO = new RsskitDAO<>(dataSource, databaseType, VkToken.class);
        this.newsFeedDAO = new RsskitDAO<>(dataSource, databaseType, VkNewsFeed.class);
    }

    @Override
    public String registerAccessToken(String accessToken) throws VkException {
        Objects.requireNonNull(accessToken);

        try {
            String token = generateToken(accessToken);

            if (tokenDAO.exists(token)) {
                return token;
            }

            VkToken obj = new VkToken();
            obj.setRsskitToken(token);
            obj.setAccessToken(accessToken);

            tokenDAO.create(obj);

            return token;
        } catch (SQLException e) {
            throw new VkException("Failed to register access token by a specified user ID: " + accessToken, e);
        }
    }

    @Override
    public String getAccessToken(String token) throws VkException {
        Objects.requireNonNull(token);

        try {
            return tokenDAO.exists(token) ? tokenDAO.get(token).getAccessToken() : null;
        } catch (SQLException e) {
            throw new VkException("Failed to get МЛ access token by a specified user ID: " + token, e);
        }
    }

    @Override
    public void setNewsFeedStartTime(String token, long startTime) throws VkException {
        Objects.requireNonNull(token);

        if(startTime < 0) {
            throw new VkException("Start time should have a valid UNIX timestamp value greater 0");
        }

        try {
            if (newsFeedDAO.exists(token)) {
                VkNewsFeed feed = newsFeedDAO.get(token);
                feed.setStartTime(startTime);

                newsFeedDAO.update(feed);
            } else {
                VkNewsFeed feed = new VkNewsFeed();
                feed.setRsskitToken(token);
                feed.setStartTime(startTime);

                newsFeedDAO.create(feed);
            }
        } catch (SQLException e) {
            throw new VkException("Failed to store VK news feed START_TIME parameter " +
                    "for a specified user ID: " + token, e);
        }
    }

    @Override
    public Long getNewsFeedStartTime(String token) throws VkException {
        Objects.requireNonNull(token);

        try {
            return newsFeedDAO.exists(token) ? newsFeedDAO.get(token).getStartTime() : null;
        } catch (SQLException e) {
            throw new VkException("Failed to get VK news feed START_TIME parameter " +
                    "by a specified user ID: " + token, e);
        }
    }
}
