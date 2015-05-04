package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rest.instagram.InstagramAPI;
import com.rhcloud.igorbotian.rsskit.rest.instagram.InstagramAPIImpl;
import com.rhcloud.igorbotian.rsskit.rest.instagram.InstagramException;
import com.rhcloud.igorbotian.rsskit.rest.instagram.InstagramFeed;
import com.rhcloud.igorbotian.rsskit.rss.instagram.InstagramRssGenerator;
import com.rhcloud.igorbotian.rsskit.utils.Configuration;
import com.rhcloud.igorbotian.rsskit.utils.URLUtils;
import com.rometools.rome.feed.synd.SyndFeed;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class InstagramServlet extends AbstractRssServlet {

    private static final String CODE_PARAM = "code";
    private static final String ACCESS_TOKEN_PARAM = "access_token";

    private static final String CLIENT_ID = Configuration.getProperty("INSTAGRAM_CLIENT_ID");
    private static final String CLIENT_SECRET = Configuration.getProperty("INSTAGRAM_CLIENT_SECRET");

    private final InstagramAPI api = new InstagramAPIImpl();
    private final InstagramRssGenerator rssGenerator = new InstagramRssGenerator();

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

        try {
            InstagramFeed feed = api.getSelfFeed(accessToken);
            SyndFeed rss = rssGenerator.generate(feed);
            respond(rss, resp);
        } catch (InstagramException e) {
            throw new IOException("Failed to generate Instagram RSS feed", e);
        }
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
