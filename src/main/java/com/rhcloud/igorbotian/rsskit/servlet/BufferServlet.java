package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rest.buffer.BufferAPI;
import com.rhcloud.igorbotian.rsskit.rest.buffer.BufferAPIImpl;
import com.rhcloud.igorbotian.rsskit.rest.buffer.BufferException;
import com.rhcloud.igorbotian.rsskit.rss.buffer.BufferPendingUpdatesRssGenerator;
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
import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class BufferServlet extends AbstractRssServlet {

    private static final String BUFFER_CLIENT_ID = "BUFFER_CLIENT_ID";
    private static final String BUFFER_CLIENT_SECRET = "BUFFER_CLIENT_SECRET";

    private static final String CODE_PARAM = "code";
    private static final String ACCESS_TOKEN_PARAM = "access_token";

    private static final int MAX_ENTRIES = 10;

    private static final String CLIENT_ID = Configuration.getProperty(BUFFER_CLIENT_ID);
    private static final String CLIENT_SECRET = Configuration.getProperty(BUFFER_CLIENT_SECRET);

    private static final BufferAPI api = new BufferAPIImpl();
    private static final BufferPendingUpdatesRssGenerator rssGenerator = new BufferPendingUpdatesRssGenerator();

    @Override
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Objects.requireNonNull(req);
        Objects.requireNonNull(resp);

        String code = req.getParameter(CODE_PARAM);

        if (StringUtils.isNotEmpty(code)) {
            requestAccessToken(code, req, resp);
        } else {
            String accessToken = req.getParameter(ACCESS_TOKEN_PARAM);

            if (StringUtils.isNotEmpty(accessToken)) {
                respondRss(accessToken, req, resp);
            } else {
                authorize(req, resp);
            }
        }
    }

    private void authorize(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        assert req != null;
        assert resp != null;

        try {
            resp.sendRedirect(api.authorize(CLIENT_ID, URLUtils.makeServletURL(req)).toString());
        } catch (BufferException e) {
            throw new IOException("Failed to redirect to Buffer authorization page", e);
        }
    }

    private void requestAccessToken(String code, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        assert StringUtils.isNotEmpty(code);
        assert req != null;
        assert resp != null;

        try {
            String accessToken = api.getAccessToken(CLIENT_ID, CLIENT_SECRET, URLUtils.makeServletURL(req), code);
            redirectToRss(accessToken, req, resp);
        } catch (BufferException e) {
            throw new IOException("Failed to complete Buffer authorization", e);
        }
    }

    private void redirectToRss(String accessToken, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        assert accessToken != null;
        assert req != null;
        assert resp != null;

        NameValuePair param = new BasicNameValuePair(ACCESS_TOKEN_PARAM, accessToken);
        resp.sendRedirect(
                URLUtils.makeURL(
                        URLUtils.makeServletURL(req).toString(),
                        Collections.singletonList(param)
                ).toString()
        );
    }

    private void respondRss(String accessToken, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        assert StringUtils.isNotEmpty(accessToken);
        assert req != null;
        assert resp != null;

        try {
            List<String> profileIDs = api.getProfilesIDs(accessToken);
            respondsRss(profileIDs, accessToken, req, resp);
        } catch (BufferException e) {
            throw new IOException("Failed to generate RSS from Buffer pending updates", e);
        }
    }

    private void respondsRss(List<String> profileIDS, String accessToken, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        assert profileIDS != null;
        assert StringUtils.isNotEmpty(accessToken);
        assert req != null;
        assert resp != null;

        SyndFeed rss;

        try {
            List<NameValuePair> pendingUpdates = api.getPendingUpdates(profileIDS, accessToken, MAX_ENTRIES);
            rss = rssGenerator.generate(pendingUpdates);
        } catch (BufferException e) {
            rss = rssGenerator.error(e);
        }

        respond(rss, resp);
    }
}
