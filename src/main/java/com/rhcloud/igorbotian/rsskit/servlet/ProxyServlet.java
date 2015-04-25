package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.proxy.HttpLinkMapper;
import com.rhcloud.igorbotian.rsskit.proxy.Proxy;
import com.rhcloud.igorbotian.rsskit.proxy.ProxyFactory;
import org.apache.http.client.utils.URIBuilder;

import javax.servlet.ServletException;
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
public class ProxyServlet extends RssKitServlet {

    private static final String LINKS_PARAM = "links";
    private static final Proxy rawProxy = ProxyFactory.raw();
    private Proxy continuousProxy;

    @Override
    protected void processRequest(URL url, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Objects.requireNonNull(url);
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        Proxy proxy = Boolean.parseBoolean(request.getParameter(LINKS_PARAM)) ? getContinuousProxy(request) : rawProxy;
        proxy.transfer(url, response);
    }

    private synchronized Proxy getContinuousProxy(HttpServletRequest request) throws MalformedURLException {
        assert request != null;

        if(continuousProxy == null) {
            String host = request.getServerName();
            int port = request.getServerPort();
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
