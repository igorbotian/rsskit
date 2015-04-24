package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.proxy.HttpLinkMapper;
import com.rhcloud.igorbotian.rsskit.proxy.Proxy;
import com.rhcloud.igorbotian.rsskit.proxy.ProxyFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class ProxyServlet extends HttpServlet {

    private static final String URL_PARAM = "url";
    private static final String LINKS_PARAM = "links";
    private static final Proxy rawProxy = ProxyFactory.raw();
    private Proxy continuousProxy;

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

        if (StringUtils.isNotEmpty(url)) {
            Proxy proxy = Boolean.parseBoolean(req.getParameter(LINKS_PARAM)) ? getContinuousProxy(req) : rawProxy;
            proxy.transfer(new URL(url), resp);
        }
    }

    private synchronized Proxy getContinuousProxy(HttpServletRequest request) throws MalformedURLException {
        assert request != null;

        if(continuousProxy == null) {
            String host = request.getServerName();
            int port = request.getLocalPort();
            String path = getServletContext().getContextPath();
            String name = getServletName();
            continuousProxy = ProxyFactory.continuous(new HttpLinkMapperImpl(new URL("http", host, port, path + "/" + name)));
        }

        return continuousProxy;
    }

    private class HttpLinkMapperImpl implements HttpLinkMapper {

        private final URL servletURL;

        public HttpLinkMapperImpl(URL servletURL) {
            this.servletURL = Objects.requireNonNull(servletURL);
        }

        @Override
        public URL map(URL url) throws URISyntaxException, MalformedURLException {
            Objects.requireNonNull(url);

            URIBuilder builder = new URIBuilder();
            builder.setScheme(servletURL.getProtocol());
            builder.setHost(servletURL.getHost());
            builder.setPort(servletURL.getPort());
            builder.setPath(servletURL.getPath());
            builder.addParameter(LINKS_PARAM, Boolean.TRUE.toString());
            builder.addParameter(URL_PARAM, url.toString());

            return builder.build().toURL();
        }
    }
}
