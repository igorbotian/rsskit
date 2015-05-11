package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterTimeline;
import com.rhcloud.igorbotian.rsskit.rss.RssGenerator;
import com.rhcloud.igorbotian.rsskit.rss.twitter.TwitterReadingListRssGenerator;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterReadingListServlet extends TwitterServlet {

    private final RssGenerator<TwitterTimeline> rssGenerator = new TwitterReadingListRssGenerator();

    @Override
    protected RssGenerator<TwitterTimeline> rssGenerator() {
        return rssGenerator;
    }
}
