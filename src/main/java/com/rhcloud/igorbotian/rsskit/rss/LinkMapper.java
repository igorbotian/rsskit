package com.rhcloud.igorbotian.rsskit.rss;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public interface LinkMapper {

    URL map(URL link) throws URISyntaxException, MalformedURLException;
}
