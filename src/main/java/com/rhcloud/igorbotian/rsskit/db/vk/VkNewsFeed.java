package com.rhcloud.igorbotian.rsskit.db.vk;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
@DatabaseTable(tableName = "vk_news_feed")
class VkNewsFeed {

    @DatabaseField(columnName = "rsskit_token", id = true)
    private String rsskitToken;

    @DatabaseField(columnName = "start_time")
    private Long startTime;

    public String getRsskitToken() {
        return rsskitToken;
    }

    public void setRsskitToken(String token) {
        this.rsskitToken = token;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }
}
