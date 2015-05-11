package com.rhcloud.igorbotian.rsskit.db.twitter;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
@DatabaseTable(tableName = "twitter_home_timeline")
class TwitterHomeTimeline {

    @DatabaseField(columnName = "rsskit_token", id = true)
    private String rsskitToken;

    @DatabaseField(columnName = "since_id")
    private String sinceID;


    public String getRsskitToken() {
        return rsskitToken;
    }

    public void setRsskitToken(String rsskitToken) {
        this.rsskitToken = rsskitToken;
    }

    public String getSinceID() {
        return sinceID;
    }

    public void setSinceID(String sinceID) {
        this.sinceID = sinceID;
    }
}
