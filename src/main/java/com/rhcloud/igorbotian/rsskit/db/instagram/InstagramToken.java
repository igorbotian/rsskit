package com.rhcloud.igorbotian.rsskit.db.instagram;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
@DatabaseTable(tableName = "instagram_token")
class InstagramToken {

    @DatabaseField(id = true, columnName = "rsskit_token")
    private String rsskitToken;

    @DatabaseField(columnName = "access_token", unique = true)
    private String accessToken;

    public String getRsskitToken() {
        return rsskitToken;
    }

    public void setRsskitToken(String rsskitToken) {
        this.rsskitToken = rsskitToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
