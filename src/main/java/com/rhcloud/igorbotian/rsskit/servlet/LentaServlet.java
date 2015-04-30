package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rss.lenta.LentaRssFeedModifier;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class LentaServlet extends RssFilteringServlet {

    public LentaServlet() throws MalformedURLException {
        super(new URL("http://lenta.ru/rss"), new LentaRssFeedModifier());
    }
}
