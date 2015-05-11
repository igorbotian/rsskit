package com.rhcloud.igorbotian.rsskit.db.instagram;

import com.j256.ormlite.db.DatabaseType;
import com.rhcloud.igorbotian.rsskit.db.RsskitDAO;
import org.apache.tomcat.jdbc.pool.DataSource;

import java.sql.SQLException;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class InstagramSelfFeedDAO extends RsskitDAO<InstagramSelfFeed> {

    public InstagramSelfFeedDAO(DataSource dataSource, DatabaseType dbType) throws SQLException {
        super(dataSource, dbType, InstagramSelfFeed.class);
    }
}
