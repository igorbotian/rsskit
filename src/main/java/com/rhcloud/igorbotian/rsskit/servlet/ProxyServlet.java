package com.rhcloud.igorbotian.rsskit.servlet;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public abstract class ProxyServlet extends RssKitServlet {

    protected static final String URL_PARAM = "url";

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        assert req != null;
        assert resp != null;

        String url = req.getParameter(URL_PARAM);

        if (StringUtils.isNotEmpty(url)) {
            processRequest(new URL(url), req, resp);
        }
    }

    protected abstract void processRequest(URL url, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException;
}
