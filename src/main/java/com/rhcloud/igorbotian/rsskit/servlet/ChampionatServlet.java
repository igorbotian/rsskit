package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.championat.ChampionatFilter;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class ChampionatServlet extends AbstractRssFilteringServlet {

    public ChampionatServlet() {
        super("http://www.championat.com/xml/rss_football.xml", new ChampionatFilter());
    }
}
