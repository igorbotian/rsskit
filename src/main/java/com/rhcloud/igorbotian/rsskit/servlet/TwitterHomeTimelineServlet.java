package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterTimeline;
import com.rhcloud.igorbotian.rsskit.rss.RssGenerator;
import com.rhcloud.igorbotian.rsskit.rss.twitter.TwitterHomeTimeLineRssGenerator;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterHomeTimelineServlet extends TwitterServlet {

    private static final RssGenerator<TwitterTimeline> rssGenerator = new TwitterHomeTimeLineRssGenerator();

    @Override
    protected RssGenerator<TwitterTimeline> rssGenerator() {
        return rssGenerator;
    }
}
