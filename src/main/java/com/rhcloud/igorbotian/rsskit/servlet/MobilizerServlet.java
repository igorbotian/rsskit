package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public abstract class MobilizerServlet extends ProxyServlet {

    private static final String HTML_CONTENT_TYPE = "text/html";
    private final Mobilizer mobilizer;

    public MobilizerServlet(Mobilizer mobilizer) {
        this.mobilizer = Objects.requireNonNull(mobilizer);
    }

    @Override
    protected void processRequest(URL url, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Objects.requireNonNull(url);
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        byte[] data = mobilizer.mobilize(url).getBytes(StandardCharsets.UTF_8);
        respond(data, HTML_CONTENT_TYPE, StandardCharsets.UTF_8, response);
    }
}
