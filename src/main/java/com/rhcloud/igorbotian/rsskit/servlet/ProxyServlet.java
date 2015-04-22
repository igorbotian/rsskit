package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.utils.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        assert req != null;
        assert resp != null;

        String url = req.getParameter(URL_PARAM);

        if (url != null && !url.isEmpty()) {
            processProxyRequest(new URL(url), resp);
        }
    }

    private void processProxyRequest(URL url, HttpServletResponse resp) throws IOException {
        assert url != null;
        assert resp != null;

        checkProtocolSupported(url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        processProxyRequest(conn, resp);
    }

    private void checkProtocolSupported(URL url) throws IOException {
        if (!"http".equals(url.getProtocol()) && !"https".equals(url.getProtocol())) {
            throw new IOException("Specified URL has unsupported protocol: " + url.toString());
        }
    }

    private void processProxyRequest(HttpURLConnection src, HttpServletResponse dest) throws IOException {
        assert src != null;
        assert dest != null;

        dest.setContentType(src.getContentType());
        dest.setContentLength(src.getContentLength());
        dest.setStatus(src.getResponseCode());

        for (String header : src.getHeaderFields().keySet()) {
            dest.addHeader(header, src.getHeaderField(header));
        }

        IOUtils.copy(src.getInputStream(), dest.getOutputStream());
    }
}
