package com.rhcloud.igorbotian.rsskit.proxy;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public interface HttpLinkMapper {

    URL map(URL url) throws URISyntaxException, MalformedURLException;
}
