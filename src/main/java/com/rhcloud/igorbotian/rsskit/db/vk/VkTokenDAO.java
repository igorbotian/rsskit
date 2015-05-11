package com.rhcloud.igorbotian.rsskit.db.vk;

import com.j256.ormlite.db.DatabaseType;
import com.rhcloud.igorbotian.rsskit.db.RsskitDAO;
import org.apache.tomcat.jdbc.pool.DataSource;

import java.sql.SQLException;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class VkTokenDAO extends RsskitDAO<VkToken> {

    public VkTokenDAO(DataSource dataSource, DatabaseType dbType) throws SQLException {
        super(dataSource, dbType, VkToken.class);
    }
}
