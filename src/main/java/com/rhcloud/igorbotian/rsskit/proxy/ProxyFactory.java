package com.rhcloud.igorbotian.rsskit.proxy;

import java.util.Map;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public final class ProxyFactory {

    private static final Proxy raw = new RawProxy();

    private ProxyFactory() {
        //
    }

    public static Proxy raw() {
        return raw;
    }

    public static Proxy continuous(HttpLinkMapper mapper) {
        return new ContinuousProxy(mapper);
    }

    public static Proxy continuous(HttpLinkMapper mapper, Map<String, String> mappedLinks) {
        return new ContinuousProxy(mapper, mappedLinks);
    }

    public static Proxy mobilizer(String serviceURL, HttpLinkMapper mapper) {
        return new MobilizerProxy(serviceURL, mapper);
    }
}
