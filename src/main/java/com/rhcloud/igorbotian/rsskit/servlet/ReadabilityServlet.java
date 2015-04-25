package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.proxy.Mobilizers;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class ReadabilityServlet extends MobilizerServlet {

    public ReadabilityServlet() {
        super(Mobilizers.READABILITY_SERVICE_URL);
    }
}
