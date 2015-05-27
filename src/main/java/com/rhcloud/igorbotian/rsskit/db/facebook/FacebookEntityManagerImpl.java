package com.rhcloud.igorbotian.rsskit.db.facebook;

import com.rhcloud.igorbotian.rsskit.db.RsskitDAO;
import com.rhcloud.igorbotian.rsskit.db.RsskitDataSource;
import com.rhcloud.igorbotian.rsskit.db.RsskitEntityManager;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookException;

import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class FacebookEntityManagerImpl extends RsskitEntityManager implements FacebookEntityManager {

    private final RsskitDAO<FacebookToken> tokenDAO;

    public FacebookEntityManagerImpl(RsskitDataSource source) throws SQLException {
        super(source);

        this.tokenDAO = new RsskitDAO<>(dataSource, databaseType, FacebookToken.class);
    }

    @Override
    public String registerAccessToken(String accessToken, Date expiredDate) throws FacebookException {
        Objects.requireNonNull(accessToken);
        Objects.requireNonNull(expiredDate);

        try {
            String token = generateToken(accessToken);

            if (tokenDAO.exists(token)) {
                FacebookToken obj = tokenDAO.get(token);

                if (!obj.getExpiredDate().equals(expiredDate)) {
                    obj.setExpiredDate(expiredDate);
                    tokenDAO.update(obj);
                }

                return token;
            }

            FacebookToken obj = new FacebookToken();
            obj.setRsskitToken(token);
            obj.setAccessToken(accessToken);

            tokenDAO.create(obj);

            return token;
        } catch (SQLException e) {
            throw new FacebookException("Failed to register access token: " + accessToken, e);
        }
    }

    @Override
    public String getAccessToken(String token) throws FacebookException {
        Objects.requireNonNull(token);

        try {
            return tokenDAO.exists(token) ? tokenDAO.get(token).getAccessToken() : null;
        } catch (SQLException e) {
            throw new FacebookException("Failed to get Facebook access token by a specified token: " + token, e);
        }
    }

    @Override
    public boolean isAcessTokenExpired(String token) throws FacebookException {
        Objects.requireNonNull(token);

        try {
            if (!tokenDAO.exists(token)) {
                throw new FacebookException("No Facebook access token is registered by a specified user ID: " + token);
            }

            Date now = new Date();
            return now.after(tokenDAO.get(token).getExpiredDate());
        } catch (SQLException e) {
            throw new FacebookException("Failed to get Facebook access token by a specified user ID: " + token, e);
        }
    }
}
