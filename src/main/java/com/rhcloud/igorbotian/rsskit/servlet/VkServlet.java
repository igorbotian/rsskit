package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rest.vk.VkAPI;
import com.rhcloud.igorbotian.rsskit.rest.vk.VkAPIImpl;
import com.rhcloud.igorbotian.rsskit.rest.vk.VkException;
import com.rhcloud.igorbotian.rsskit.rest.vk.VkFeed;
import com.rhcloud.igorbotian.rsskit.rss.vk.VkNewsFeedRssGenerator;
import com.rhcloud.igorbotian.rsskit.utils.Configuration;
import com.rhcloud.igorbotian.rsskit.utils.URLUtils;
import com.rometools.rome.feed.synd.SyndFeed;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkServlet extends AbstractRssServlet {

    private static final Logger LOGGER = LogManager.getLogger(VkServlet.class);

    private static final String CODE_PARAM = "code";
    private static final String ACCESS_TOKEN_PARAM = "access_token";

    private static final String CLIENT_ID = Configuration.getProperty("VK_CLIENT_ID");
    private static final String CLIENT_SECRET = Configuration.getProperty("VK_CLIENT_SECRET");

    private static final Set<String> PERMISSIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "friends",
            "wall",
            "offline"
    )));

    private VkAPI api;
    private VkNewsFeedRssGenerator rssGenerator = new VkNewsFeedRssGenerator();

    @Override
    public void init() throws ServletException {
        super.init();

        try {
            api = new VkAPIImpl(dataSource());
        } catch (SQLException | VkException e) {
            LOGGER.fatal("Failed to initialize VK entity manager", e);
            throw new ServletException("Failed to initialize DB", e);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Objects.requireNonNull(req);
        Objects.requireNonNull(resp);

        String accessToken = req.getParameter(ACCESS_TOKEN_PARAM);

        if (StringUtils.isNotEmpty(accessToken)) {
            respondRss(accessToken, resp);
        } else {
            String code = req.getParameter(CODE_PARAM);

            if (StringUtils.isNotEmpty(code)) {
                redirectToRss(requestAccessToken(code, req), req, resp);
            } else {
                authorize(req, resp);
            }
        }
    }

    private void respondRss(String accessToken, HttpServletResponse resp) throws IOException {
        assert accessToken != null;
        assert resp != null;

        SyndFeed rss;

        try {
            VkFeed newsFeed = api.getNewsFeed(accessToken);
            rss = rssGenerator.generate(newsFeed);
        } catch (VkException e) {
            LOGGER.error("Failed to get VK news feed and generate appropriate RSS feed", e);
            rss = rssGenerator.error(e);
        }

        respond(rss, resp);
    }

    private String requestAccessToken(String code, HttpServletRequest req) throws IOException {
        assert code != null;
        assert req != null;

        try {
            return api.requestAccessToken(CLIENT_ID, CLIENT_SECRET, code);
        } catch (VkException e) {
            throw new IOException("Failed to request VK access token", e);
        }
    }

    private void redirectToRss(String accessToken, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        assert accessToken != null;
        assert req != null;
        assert resp != null;

        URL servletURL = URLUtils.makeServletURL(req);
        resp.sendRedirect(URLUtils.makeURL(
                servletURL.toString(),
                Collections.<NameValuePair>singletonList(new BasicNameValuePair("access_token", accessToken))
        ).toString());
    }

    private void authorize(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        assert req != null;
        assert resp != null;

        try {
            URL authURL = api.getAuthorizationURL(CLIENT_ID, PERMISSIONS);
            resp.sendRedirect(authURL.toString());
        } catch (VkException e) {
            throw new IOException("Failed to redirect to VK authorization page", e);
        }
    }
}
