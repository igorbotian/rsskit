package com.rhcloud.igorbotian.rsskit.rss;

import com.rometools.rome.feed.synd.SyndFeed;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public interface RssModifier {

    SyndFeed apply(SyndFeed original);
}
