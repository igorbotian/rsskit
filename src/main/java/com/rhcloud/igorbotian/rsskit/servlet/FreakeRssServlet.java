package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.freake.Freake;
import com.rhcloud.igorbotian.rsskit.freake.FreakeRssFeed;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class FreakeRssServlet extends AbstractRssServlet {

    private static FreakeRssFeed feed;

    @Override
    public void init() throws ServletException {
        synchronized (FreakeRssServlet.class) {
            if (feed == null) {
                try {
                    feed = new FreakeRssFeed(new Freake());
                } catch (IOException e) {
                    throw new ServletException("Failed to initialize RSS feed", e);
                }
            }
        }
    }

    @Override
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        assert req != null;
        assert resp != null;

        transfer(feed.get(), resp);
    }
}
