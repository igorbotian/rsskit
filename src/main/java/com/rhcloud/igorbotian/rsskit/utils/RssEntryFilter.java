package com.rhcloud.igorbotian.rsskit.utils;

import com.rometools.rome.feed.synd.SyndEntry;

/**
 * @author Igor Botian
 */
public interface RssEntryFilter {

    boolean apply(SyndEntry entry);
}
