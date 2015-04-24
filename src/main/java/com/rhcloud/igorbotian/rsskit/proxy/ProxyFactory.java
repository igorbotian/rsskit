package com.rhcloud.igorbotian.rsskit.proxy;

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
}
