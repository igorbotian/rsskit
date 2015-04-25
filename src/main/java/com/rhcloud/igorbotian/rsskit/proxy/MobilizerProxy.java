package com.rhcloud.igorbotian.rsskit.proxy;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class MobilizerProxy implements Proxy {

    private static final Map<String, String> MAPPED_LINKS;

    static {
        Map<String, String> mappedLinks = new HashMap<>();
        mappedLinks.put("link", "href");
        mappedLinks.put("script", "src");
        mappedLinks.put("img", "src");

        MAPPED_LINKS = Collections.unmodifiableMap(mappedLinks);
    }

    private final String serviceURL;
    private final Proxy proxy;

    public MobilizerProxy(String serviceURL, HttpLinkMapper mapper) {
        this.serviceURL = Objects.requireNonNull(serviceURL);
        this.proxy = ProxyFactory.continuous(mapper, MAPPED_LINKS);
    }

    @Override
    public void transfer(URL src, HttpServletResponse dest) throws IOException {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dest);

        URL url = mobilizingURL(src);
        proxy.transfer(url, dest);
    }

    private URL mobilizingURL(URL url) throws MalformedURLException {
        assert url != null;
        return new URL(serviceURL + url.toString());
    }
}
