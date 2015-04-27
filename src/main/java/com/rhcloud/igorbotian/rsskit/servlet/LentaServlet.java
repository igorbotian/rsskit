package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.lenta.LentaFilter;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class LentaServlet extends AbstractRssFilteringServlet {

    public LentaServlet() {
        super("http://lenta.ru/rss", new LentaFilter());
    }
}
