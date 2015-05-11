package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rest.instagram.InstagramAPI;
import com.rhcloud.igorbotian.rsskit.rest.instagram.InstagramAPIImpl;
import com.rhcloud.igorbotian.rsskit.rest.instagram.InstagramException;
import com.rhcloud.igorbotian.rsskit.rest.instagram.InstagramFeed;
import com.rhcloud.igorbotian.rsskit.rss.instagram.InstagramSelfFeedRssGenerator;
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
import java.util.Collections;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class InstagramServlet extends AbstractRssServlet {

    private static final Logger LOGGER = LogManager.getLogger(InstagramServlet.class);

    private static final String CODE_PARAM = "code";
    private static final String ACCESS_TOKEN_PARAM = "access_token";

    private static final String CLIENT_ID = Configuration.getProperty("INSTAGRAM_CLIENT_ID");
    private static final String CLIENT_SECRET = Configuration.getProperty("INSTAGRAM_CLIENT_SECRET");

    private InstagramAPI api;
    private final InstagramSelfFeedRssGenerator rssGenerator = new InstagramSelfFeedRssGenerator();

    @Override
    public void init() throws ServletException {
        super.init();

        try {
            api = new InstagramAPIImpl(dataSource());
        } catch (InstagramException | SQLException e) {
            LOGGER.fatal("Failed to initialize Instagram entity manager", e);
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
            String authorizationCode = req.getParameter(CODE_PARAM);

            if (StringUtils.isNotEmpty(authorizationCode)) {
                String token = requestAccessToken(CLIENT_ID, CLIENT_SECRET, authorizationCode, req);
                redirectToRss(token, req, resp);
            } else {
                authorize(CLIENT_ID, URLUtils.makeServletURL(req), resp);
            }
        }
    }

    private void respondRss(String accessToken, HttpServletResponse resp) throws IOException {
        assert accessToken != null;
        assert resp != null;

        SyndFeed rss;

        try {
            InstagramFeed feed = api.getSelfFeed(accessToken);
            rss = rssGenerator.generate(feed);
        } catch (InstagramException e) {
            LOGGER.error("Failed to get Instagram self feed and generate appropriate RSS feed", e);
            rss = rssGenerator.error(e);
        }

        respond(rss, resp);
    }

    private String requestAccessToken(String clientID, String clientSecret, String authorizationCode,
                                      HttpServletRequest req) throws IOException {
        assert clientID != null;
        assert clientSecret != null;
        assert authorizationCode != null;
        assert req != null;

        try {
            return api.requestAccessToken(clientID, clientSecret, authorizationCode, URLUtils.makeServletURL(req));
        } catch (InstagramException e) {
            throw new IOException("Failed to request Instagram access token", e);
        }
    }

    private void redirectToRss(String accessToken, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        assert accessToken != null;
        assert req != null;
        assert resp != null;

        URL servletURL = URLUtils.makeServletURL(req);
        resp.sendRedirect(URLUtils.makeURL(
                servletURL.toString(),
                Collections.<NameValuePair>singletonList(new BasicNameValuePair(ACCESS_TOKEN_PARAM, accessToken))
        ).toString());
    }

    private void authorize(String clientID, URL callbackURL, HttpServletResponse resp) throws IOException {
        assert clientID != null;
        assert callbackURL != null;
        assert resp != null;

        try {
            URL authorizationURL = api.getAuthorizationURL(clientID, callbackURL);
            resp.sendRedirect(authorizationURL.toString());
        } catch (InstagramException e) {
            throw new IOException("Failed to redirect to Instagram authorization page", e);
        }
    }
}
