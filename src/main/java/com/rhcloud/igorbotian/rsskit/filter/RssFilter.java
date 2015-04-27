package com.rhcloud.igorbotian.rsskit.filter;

import com.rometools.rome.feed.synd.SyndFeed;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public interface RssFilter {

    SyndFeed apply(SyndFeed feed);
}
