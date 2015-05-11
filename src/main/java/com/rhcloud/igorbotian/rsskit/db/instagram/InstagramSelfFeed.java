package com.rhcloud.igorbotian.rsskit.db.instagram;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
@DatabaseTable(tableName = "instagram_self_feed")
class InstagramSelfFeed {

    @DatabaseField(id = true, columnName = "rsskit_token")
    private String rsskitToken;

    @DatabaseField(columnName = "min_id")
    private String minID;

    public String getRsskitToken() {
        return rsskitToken;
    }

    public void setRsskitToken(String rsskitToken) {
        this.rsskitToken = rsskitToken;
    }

    public String getMinID() {
        return minID;
    }

    public void setMinID(String minID) {
        this.minID = minID;
    }
}
