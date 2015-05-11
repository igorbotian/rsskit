package com.rhcloud.igorbotian.rsskit.db.vk;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
@DatabaseTable(tableName = "vk_token")
class VkToken {

    @DatabaseField(columnName = "rsskit_token", id = true)
    private String rsskitToken;

    @DatabaseField(columnName = "access_token")
    private String accessToken;

    public String getRsskitToken() {
        return rsskitToken;
    }

    public void setRsskitToken(String token) {
        this.rsskitToken = token;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String token) {
        this.accessToken = token;
    }
}
