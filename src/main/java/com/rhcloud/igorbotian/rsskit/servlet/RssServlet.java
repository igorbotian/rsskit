package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.proxy.ProxyFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class RssServlet extends ProxyServlet {

    @Override
    protected void processRequest(URL url, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Objects.requireNonNull(url);
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        ProxyFactory.rss().transfer(url, response);
    }
}
