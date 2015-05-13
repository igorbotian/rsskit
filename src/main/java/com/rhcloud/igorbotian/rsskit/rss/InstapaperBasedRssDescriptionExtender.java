package com.rhcloud.igorbotian.rsskit.rss;

import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizers;

import java.io.IOException;
import java.net.URL;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class InstapaperBasedRssDescriptionExtender extends RssDescriptionExtender {

    @Override
    protected String mobilize(URL url) throws IOException {
        return Mobilizers.instapaper().mobilize(url);
    }
}
