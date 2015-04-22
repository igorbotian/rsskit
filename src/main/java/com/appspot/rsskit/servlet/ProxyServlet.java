package com.appspot.rsskit.servlet;

import com.appspot.rsskit.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class ProxyServlet extends HttpServlet {

    private static final String URL_PARAM = "url";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getParameter(URL_PARAM);

        if (StringUtils.isNotEmpty(url)) {
            processProxyRequest(new URL(url), resp);
        }
    }

    private void processProxyRequest(URL url, HttpServletResponse resp)
            throws ServletException, IOException {

        assert url != null;

        InputStream inputStream = null;

        try {
            URLConnection conn = url.openConnection();
            conn.setDoInput(true);

            inputStream = conn.getInputStream();
            IOUtils.copy(conn.getInputStream(), resp.getOutputStream());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
