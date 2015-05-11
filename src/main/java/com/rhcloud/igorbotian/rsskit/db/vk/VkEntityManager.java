package com.rhcloud.igorbotian.rsskit.db.vk;

import com.rhcloud.igorbotian.rsskit.rest.vk.VkException;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public interface VkEntityManager {

    String registerAccessToken(String accessToken) throws VkException;

    String getAccessToken(String token) throws VkException;

    void setNewsFeedStartTime(String token, long startTime) throws VkException;

    Long getNewsFeedStartTime(String token) throws VkException;
}
