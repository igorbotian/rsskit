package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rss.championat.ChampionatRssFeedModifier;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class ChampionatServlet extends RssFilteringServlet {

    public ChampionatServlet() throws MalformedURLException {
        super(new URL("http://www.championat.com/xml/rss_football.xml"), new ChampionatRssFeedModifier());
    }
}
