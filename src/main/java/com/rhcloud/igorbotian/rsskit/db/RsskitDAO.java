package com.rhcloud.igorbotian.rsskit.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.jdbc.DataSourceConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.apache.tomcat.jdbc.pool.DataSource;

import java.sql.SQLException;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class RsskitDAO<T> {

    protected final Dao<T, String> dao;

    public RsskitDAO(DataSource dataSource, DatabaseType dbType, Class<T> entityClass) throws SQLException {
        Objects.requireNonNull(dataSource);
        Objects.requireNonNull(dbType);
        Objects.requireNonNull(entityClass);

        ConnectionSource source = new DataSourceConnectionSource(dataSource, dbType);
        dao = DaoManager.createDao(source, entityClass);

        TableUtils.createTableIfNotExists(source, entityClass);
    }

    public boolean exists(String id) throws SQLException {
        Objects.requireNonNull(id);
        return dao.idExists(id);
    }

    public T get(String id) throws SQLException {
        Objects.requireNonNull(id);
        return dao.queryForId(id);
    }

    public void create(T obj) throws SQLException {
        Objects.requireNonNull(obj);
        dao.create(obj);
    }

    public void update(T obj) throws SQLException {
        Objects.requireNonNull(obj);
        dao.update(obj);
    }
}
