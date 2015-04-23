package com.rhcloud.igorbotian.rsskit.proxy;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public interface Proxy {

    void transfer(URL src, HttpServletResponse dest) throws IOException;
}
