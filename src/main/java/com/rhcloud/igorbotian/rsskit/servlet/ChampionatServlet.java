package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.championat.ChampionatFilter;
import com.rhcloud.igorbotian.rsskit.proxy.RssFilteringProxy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class ChampionatServlet extends AbstractRssServlet {

    private static final String CHAMPIONAT_RSS_URL = "http://www.championat.com/xml/rss_football.xml";
    private static final RssFilteringProxy rssFilteringProxy = new RssFilteringProxy(new ChampionatFilter()) {
    };

    private URL rssURL;

    @Override
    public void init() throws ServletException {
        synchronized (ChampionatServlet.class) {
            if (rssURL == null) {
                try {
                    rssURL = new URL(CHAMPIONAT_RSS_URL);
                } catch (MalformedURLException e) {
                    throw new ServletException("Championat.com RSS link is broken: " + CHAMPIONAT_RSS_URL, e);
                }
            }
        }
    }

    @Override
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        assert req != null;
        assert resp != null;

        rssFilteringProxy.transfer(rssURL, resp);
    }
}
