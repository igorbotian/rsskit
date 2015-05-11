package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rest.OAuth10Credentials;
import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterAPI;
import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterAPIImpl;
import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterException;
import com.rhcloud.igorbotian.rsskit.rest.twitter.TwitterTimeline;
import com.rhcloud.igorbotian.rsskit.rss.RssGenerator;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public abstract class TwitterServlet extends AbstractRssServlet {

    private static final Logger LOGGER = LogManager.getLogger(TwitterServlet.class);

    private static final String ACCESS_TOKEN_PARAM = "access_token";
    private static final String OAUTH_VERIFIER_PARAM = "oauth_verifier";

    private static final String CONSUMER_KEY = Configuration.getProperty("TWITTER_CONSUMER_KEY");
    private static final String CONSUMER_SECRET = Configuration.getProperty("TWITTER_CONSUMER_SECRET");

    private static TwitterAPI api;

    @Override
    public void init() throws ServletException {
        super.init();

        try {
            if(api == null) {
                api = new TwitterAPIImpl(new OAuth10Credentials(CONSUMER_KEY, CONSUMER_SECRET), dataSource());
            }
        } catch (TwitterException | SQLException e) {
            LOGGER.fatal("Failed to initialize Twitter entity manager", e);
            throw new ServletException("Failed to initialize DB", e);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Objects.requireNonNull(req);
        Objects.requireNonNull(resp);

        String accessToken = req.getParameter(ACCESS_TOKEN_PARAM);
        String oauthVerifier = req.getParameter(OAUTH_VERIFIER_PARAM);

        if (StringUtils.isNotEmpty(accessToken)) {
            respondRss(accessToken, req, resp);
        } else if (StringUtils.isNotEmpty(oauthVerifier)) {
            redirectToRss(requestAccessToken(oauthVerifier), req, resp);
        } else {
            authorize(req, resp);
        }
    }

    private void respondRss(String token, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        assert token != null;
        assert req != null;
        assert resp != null;

        SyndFeed rss;

        try {
            TwitterTimeline timeline = api.getHomeTimeline(token);
            rss = rssGenerator().generate(timeline);
        } catch (TwitterException e) {
            LOGGER.error("Failed to get Twitter home timeline and generate appropriate RSS feed", e);
            rss = rssGenerator().error(e);
        }

        respond(rss, resp);
    }

    protected abstract RssGenerator<TwitterTimeline> rssGenerator();

    private String requestAccessToken(String oauthVerifier) throws IOException {
        assert oauthVerifier != null;

        try {
            return api.requestAccessToken(oauthVerifier);
        } catch (TwitterException e) {
            throw new IOException("Failed to request Twitter REST API access token", e);
        }
    }

    private void redirectToRss(String token, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        assert token != null;
        assert req != null;
        assert resp != null;

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("access_token", token));

        resp.sendRedirect(URLUtils.makeURL(
                URLUtils.makeServletURL(req).toString(),
                params
        ).toString());
    }

    private void authorize(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        assert req != null;
        assert resp != null;

        try {
            resp.sendRedirect(api.getAuthorizationURL(URLUtils.makeServletURL(req)).toString());
        } catch (TwitterException e) {
            throw new IOException("Failed to redirect to Sign in with Twitter page", e);
        }
    }
}
