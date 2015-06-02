package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookPost;
import com.rhcloud.igorbotian.rsskit.rest.facebook.api.FacebookAPI;
import com.rhcloud.igorbotian.rsskit.rest.facebook.api.FacebookAPIImpl;
import com.rhcloud.igorbotian.rsskit.rss.RssGenerator;
import com.rhcloud.igorbotian.rsskit.rss.facebook.FacebookNewsFeedRssGenerator;
import com.rhcloud.igorbotian.rsskit.utils.Configuration;
import com.rhcloud.igorbotian.rsskit.utils.URLUtils;
import com.rometools.rome.feed.synd.SyndFeed;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class FacebookServlet extends AbstractRssServlet {

    private static final Logger LOGGER = LogManager.getLogger(FacebookServlet.class);

    private static final String CODE_PARAM = "code";
    private static final String ACCESS_TOKEN_PARAM = "access_token";
    private static final String NOTIFICATIONS_PARAM = "notifications";
    private static final String LIMIT_PARAM = "limit";

    private static final String APP_ID = Configuration.getProperty("FACEBOOK_APP_ID");
    private static final String APP_SECRET = Configuration.getProperty("FACEBOOK_APP_SECRET");

    private static final Set<String> PERMISSIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "public_profile",
            "read_stream",
            "manage_pages",
            "manage_notifications"
    )));
    private final RssGenerator<List<FacebookPost>> rssGenerator = new FacebookNewsFeedRssGenerator();
    private FacebookAPI api;

    @Override
    public void init() throws ServletException {
        super.init();

        try {
            api = new FacebookAPIImpl(dataSource());
        } catch (SQLException | FacebookException e) {
            LOGGER.fatal("Failed to initialize Facebook entity manager", e);
            throw new ServletException("Failed to initialize DB", e);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Objects.requireNonNull(req);
        Objects.requireNonNull(resp);

        String accessToken = req.getParameter(ACCESS_TOKEN_PARAM);
        boolean notifications = Boolean.parseBoolean(req.getParameter(NOTIFICATIONS_PARAM));

        if (StringUtils.isNotEmpty(accessToken)) {
            Integer limit = parseLimit(req.getParameter(LIMIT_PARAM));
            respondRss(accessToken, notifications, limit, resp);
        } else {
            String code = req.getParameter(CODE_PARAM);

            if (StringUtils.isNotEmpty(code)) {
                redirectToRss(requestAccessToken(code, notifications, req), notifications, req, resp);
            } else {
                authorize(notifications, req, resp);
            }
        }
    }

    private Integer parseLimit(String param) {
        try {
            if (param != null) {
                return Integer.parseInt(param);
            }
        } catch (NumberFormatException e) {
            LOGGER.trace("LIMIT integer parameter is not a number: " + param);
        }

        return null;
    }

    private void respondRss(String accessToken, boolean notifications, Integer limit, HttpServletResponse resp)
            throws IOException {

        assert accessToken != null;
        assert resp != null;

        SyndFeed rss;

        try {
            List<FacebookPost> newsFeed = notifications
                    ? api.getPostsFromNotifications(accessToken, limit)
                    : api.getNewsFeed(accessToken);
            rss = rssGenerator.generate(newsFeed);
        } catch (FacebookException e) {
            LOGGER.error("Failed to get Facebook notifications and generate appropriate RSS feed", e);
            rss = rssGenerator.error(e);
        }

        respond(rss, resp);
    }

    private String requestAccessToken(String code, boolean notifications, HttpServletRequest req) throws IOException {
        assert code != null;
        assert req != null;

        try {
            return api.requestAccessToken(APP_ID, APP_SECRET, code, makeCallbackURL(notifications, req));
        } catch (FacebookException e) {
            throw new IOException("Failed to request a Facebook access token", e);
        } catch (URISyntaxException e) {
            throw new IOException("Failed to compose a callback URL", e);
        }
    }

    private void redirectToRss(String accessToken, boolean notifications,
                               HttpServletRequest req, HttpServletResponse resp) throws IOException {

        assert accessToken != null;
        assert req != null;
        assert resp != null;

        URL servletURL = URLUtils.makeServletURL(req);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(ACCESS_TOKEN_PARAM, accessToken));

        if (notifications) {
            params.add(new BasicNameValuePair(NOTIFICATIONS_PARAM, Boolean.TRUE.toString()));
        }

        resp.sendRedirect(URLUtils.makeURL(servletURL.toString(), params).toString());
    }

    private void authorize(boolean notifications, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        assert req != null;
        assert resp != null;

        try {
            URL authURL = api.getAuthorizationURL(APP_ID, PERMISSIONS, makeCallbackURL(notifications, req));
            resp.sendRedirect(authURL.toString());
        } catch (FacebookException e) {
            throw new IOException("Failed to redirect to Facebook authorization page", e);
        } catch (URISyntaxException e) {
            throw new IOException("Failed to compose a callback URL", e);
        }
    }

    private URL makeCallbackURL(boolean notifications, HttpServletRequest request)
            throws MalformedURLException, URISyntaxException {

        assert request != null;

        URL url = URLUtils.makeServletURL(request);

        if (!notifications) {
            return url;
        }

        URIBuilder builder = new URIBuilder(url.toURI());
        builder.setParameter(NOTIFICATIONS_PARAM, Boolean.TRUE.toString());
        return builder.build().toURL();
    }
}
