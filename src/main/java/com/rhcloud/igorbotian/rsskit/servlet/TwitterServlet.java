package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rest.OAuth10Credentials;
import com.rhcloud.igorbotian.rsskit.rest.twitter.*;
import com.rhcloud.igorbotian.rsskit.rss.twitter.TwitterHomeTimelineRssGenerator;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterServlet extends AbstractRssServlet {

    private static final String ACCESS_TOKEN_PARAM = "access_token";
    private static final String TOKEN_SECRET_PARAM = "token_secret";
    private static final String OAUTH_VERIFIER_PARAM = "oauth_verifier";

    private static final String CONSUMER_KEY = Configuration.getProperty("TWITTER_CONSUMER_KEY");
    private static final String CONSUMER_SECRET = Configuration.getProperty("TWITTER_CONSUMER_SECRET");
    private static final String INSTAGRAM_ACCESS_TOKEN = Configuration.getProperty("INSTAGRAM_ACCESS_TOKEN");
    private static final TwitterHomeTimelineRssGenerator rssGenerator = new TwitterHomeTimelineRssGenerator(INSTAGRAM_ACCESS_TOKEN);

    private final TwitterAPI api = new TwitterAPIImpl(new OAuth10Credentials(CONSUMER_KEY, CONSUMER_SECRET));

    @Override
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Objects.requireNonNull(req);
        Objects.requireNonNull(resp);

        String accessToken = req.getParameter(ACCESS_TOKEN_PARAM);
        String tokenSecret = req.getParameter(TOKEN_SECRET_PARAM);
        String oauthVerifier = req.getParameter(OAUTH_VERIFIER_PARAM);

        if(StringUtils.isNotEmpty(accessToken) && StringUtils.isNotEmpty(TOKEN_SECRET_PARAM)) {
            respondRss(new OAuth10Credentials(CONSUMER_KEY, CONSUMER_SECRET, accessToken, tokenSecret), req, resp);
        } else if (StringUtils.isNotEmpty(oauthVerifier)) {
            redirectToRss(requestAccessToken(oauthVerifier), req, resp);
        } else {
            authorize(req, resp);
        }
    }

    private void respondRss(OAuth10Credentials token, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        assert token != null;
        assert req != null;
        assert resp != null;

        SyndFeed rss;

        try {
            TwitterTimeline timeline = api.getHomeTimeline(token);
            rss = rssGenerator.generate(timeline);
        } catch (TwitterException e) {
            rss = rssGenerator.error(e);
        }

        respond(rss, resp);
    }

    private OAuth10Credentials requestAccessToken(String oauthVerifier) throws IOException {
        assert oauthVerifier != null;

        try {
            return api.requestAccessToken(oauthVerifier);
        } catch (TwitterException e) {
            throw new IOException("Failed to request Twitter REST API access token", e);
        }
    }

    private void redirectToRss(OAuth10Credentials token, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        assert token != null;
        assert req != null;
        assert resp != null;

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("access_token", token.consumerKey));
        params.add(new BasicNameValuePair("token_secret", token.consumerSecret));

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
