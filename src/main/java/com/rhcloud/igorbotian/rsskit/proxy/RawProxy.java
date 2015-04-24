package com.rhcloud.igorbotian.rsskit.proxy;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class RawProxy extends HttpProxy {

    @Override
    protected void transfer(HttpURLConnection src, HttpServletResponse dest) throws IOException {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dest);

        transferHeaders(src, dest);
        transferContents(src, dest);
    }

    private void transferHeaders(HttpURLConnection src, HttpServletResponse dest) throws IOException {
        assert src != null;
        assert dest != null;

        dest.setContentType(src.getContentType());
        dest.setContentLength(src.getContentLength());
        dest.setStatus(src.getResponseCode());

        for (String header : src.getHeaderFields().keySet()) {
            dest.addHeader(header, src.getHeaderField(header));
        }
    }

    private void transferContents(HttpURLConnection src, HttpServletResponse dest) throws IOException {
        assert src != null;
        assert dest != null;

        try(InputStream is = src.getInputStream()) {
            try(OutputStream os = dest.getOutputStream()) {
                IOUtils.copy(is, os);
            }
        }
    }
}
