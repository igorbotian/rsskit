package com.rhcloud.igorbotian.rsskit.db.facebook;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
@DatabaseTable(tableName = "facebook_token")
class FacebookToken {

    @DatabaseField(columnName = "rsskit_token", id = true)
    private String rsskitToken;

    @DatabaseField(columnName = "access_token", unique = true)
    private String accessToken;

    @DatabaseField(columnName = "expires")
    private Date expiredDate;

    public void setRsskitToken(String token) {
        this.rsskitToken = token;
    }

    public String getRsskitToken() {
        return rsskitToken;
    }

    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setExpiredDate(Date date) {
        this.expiredDate = date;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }
}
