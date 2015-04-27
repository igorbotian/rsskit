package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.filter.RssFilter;
import com.rhcloud.igorbotian.rsskit.proxy.RssFilteringProxy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class AbstractRssFilteringServlet extends AbstractRssServlet {

    private final String feedURL;

    private final RssFilteringProxy rssFilteringProxy;
    private URL rssURL;

    public AbstractRssFilteringServlet(String rssURL, RssFilter rssFilter) {
        this.feedURL = Objects.requireNonNull(rssURL);
        this.rssFilteringProxy = new RssFilteringProxy(Objects.requireNonNull(rssFilter)) {
        };;
    }

    @Override
    public void init() throws ServletException {
        synchronized (LentaServlet.class) {
            if (rssURL == null) {
                try {
                    rssURL = new URL(feedURL);
                } catch (MalformedURLException e) {
                    throw new ServletException("Lenta.ru RSS link is broken: " + feedURL, e);
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
