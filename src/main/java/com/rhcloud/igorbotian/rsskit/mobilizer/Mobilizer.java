package com.rhcloud.igorbotian.rsskit.mobilizer;

import java.io.IOException;
import java.net.URL;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public interface Mobilizer {

    String mobilize(URL url) throws IOException;
}
