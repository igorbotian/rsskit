package com.rhcloud.igorbotian.rsskit.db.twitter;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
@DatabaseTable(tableName = "twitter_token")
class TwitterToken {

    @DatabaseField(columnName = "rsskit_token", id = true)
    private String rsskitToken;

    @DatabaseField(columnName = "token_secret")
    private String tokenSecret;

    @DatabaseField(columnName = "access_token", unique = true)
    private String accessToken;

    public String getRsskitToken() {
        return rsskitToken;
    }

    public void setRsskitToken(String token) {
        this.rsskitToken = token;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public void setTokenSecret(String tokenSecret) {
        this.tokenSecret = tokenSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String token) {
        this.accessToken = token;
    }
}
