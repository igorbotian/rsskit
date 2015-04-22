package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.utils.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    private void processProxyRequest(HttpURLConnection src, HttpServletResponse dst) throws IOException {
        assert src != null;
        assert dst != null;

        transferHeaders(src, dst);
        transferContents(src, dst);
    }

    private void transferHeaders(HttpURLConnection src, HttpServletResponse dst) throws IOException {
        assert src != null;
        assert dst != null;

        dst.setContentType(src.getContentType());
        dst.setContentLength(src.getContentLength());
        dst.setStatus(src.getResponseCode());

        for (String header : src.getHeaderFields().keySet()) {
            dst.addHeader(header, src.getHeaderField(header));
        }
    }

    private void transferContents(HttpURLConnection src, HttpServletResponse dst) throws IOException {
        assert src != null;
        assert dst != null;

        InputStream is = null;

        try {
            is = src.getInputStream();
            OutputStream os = null;

            try {
                os = dst.getOutputStream();
                IOUtils.copy(is, dst.getOutputStream());
            } finally {
                if(os != null) {
                    os.close();
                }
            }
        } finally {
            if(is != null) {
                is.close();
            }
        }
    }
}
