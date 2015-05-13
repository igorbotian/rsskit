package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rss.meduza.MeduzaRssFeedModifier;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class MeduzaServlet extends RssFilteringServlet {

    public MeduzaServlet() throws MalformedURLException {
        super(new URL("http://meduza.io/rss/all"), new MeduzaRssFeedModifier());
    }
}
