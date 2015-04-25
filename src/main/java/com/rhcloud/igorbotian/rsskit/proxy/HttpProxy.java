package com.rhcloud.igorbotian.rsskit.proxy;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
abstract class HttpProxy implements Proxy {

    @Override
    public void transfer(URL src, HttpServletResponse dest) throws IOException {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dest);

        checkProtocolSupported(src);
        HttpURLConnection conn = (HttpURLConnection) src.openConnection();
        transfer(conn, dest);
    }

    private void checkProtocolSupported(URL url) throws IOException {
        if (!"http".equals(url.getProtocol()) && !"https".equals(url.getProtocol())) {
            throw new IOException("Specified URL has unsupported protocol: " + url.toString());
        }
    }

    protected abstract void transfer(HttpURLConnection src, HttpServletResponse dest) throws IOException;
}
