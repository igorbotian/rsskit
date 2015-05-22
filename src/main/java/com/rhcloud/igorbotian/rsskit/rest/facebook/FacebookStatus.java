package com.rhcloud.igorbotian.rsskit.rest.facebook;

import java.util.Date;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class FacebookStatus extends FacebookPost {

    public FacebookStatus(String id, Date createdTime, FacebookProfile from,
                          String caption, String message) {

        super(id, createdTime, from, caption, message, FacebookPostType.STATUS);
    }
}
