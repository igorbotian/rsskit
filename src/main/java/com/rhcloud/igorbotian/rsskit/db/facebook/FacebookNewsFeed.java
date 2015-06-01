package com.rhcloud.igorbotian.rsskit.db.facebook;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
@DatabaseTable(tableName = "facebook_news_feed")
public class FacebookNewsFeed {

    @DatabaseField(columnName = "rsskit_token", id = true)
    private String rsskitToken;

    @DatabaseField(columnName = "since", unique = true)
    private Date since;

    public void setRsskitToken(String token) {
        this.rsskitToken = token;
    }

    public String getRsskitToken() {
        return rsskitToken;
    }

    public void setSince(Date since) {
        this.since = since;
    }

    public Date getSince() {
        return since;
    }
}
