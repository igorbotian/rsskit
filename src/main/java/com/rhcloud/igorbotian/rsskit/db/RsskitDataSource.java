package com.rhcloud.igorbotian.rsskit.db;

import com.j256.ormlite.db.DatabaseType;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.sql.SQLException;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class RsskitDataSource {

    private final DataSource dataSource;
    private final DatabaseType databaseType;

    public RsskitDataSource(String dbURL, String dbUsername, String dbPassword, DatabaseType databaseType) {
        Objects.requireNonNull(dbURL);
        Objects.requireNonNull(dbUsername);
        Objects.requireNonNull(dbPassword);
        Objects.requireNonNull(databaseType);

        PoolProperties props = new PoolProperties();
        props.setUrl(dbURL);
        props.setUsername(dbUsername);
        props.setPassword(dbPassword);
        props.setCommitOnReturn(true);
        props.setDefaultAutoCommit(true);
        props.setJdbcInterceptors("ConnectionState;StatementFinalizer");
        props.setDriverClassName("org.h2.Driver");
        props.setMaxActive(10);
        props.setMaxIdle(5);
        props.setMinIdle(2);
        props.setValidationQuery("select 1");

        dataSource = new DataSource();
        dataSource.setPoolProperties(props);
        this.databaseType = databaseType;
    }

    public DataSource get() throws SQLException {
        return dataSource;
    }

    public DatabaseType databaseType() {
        return databaseType;
    }
}
