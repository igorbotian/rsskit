package com.rhcloud.igorbotian.rsskit.db;

import com.j256.ormlite.db.DatabaseType;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tomcat.jdbc.pool.DataSource;

import java.sql.SQLException;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public abstract class RsskitEntityManager {

    protected DataSource dataSource;
    protected DatabaseType databaseType;

    public RsskitEntityManager(RsskitDataSource dataSource) throws SQLException {
        Objects.requireNonNull(dataSource);

        this.dataSource = dataSource.get();
        this.databaseType = dataSource.databaseType();
    }

    protected String generateToken(String accessToken) {
        assert accessToken != null;
        return Base64.encodeBase64String(DigestUtils.md5(accessToken));
    }
}
