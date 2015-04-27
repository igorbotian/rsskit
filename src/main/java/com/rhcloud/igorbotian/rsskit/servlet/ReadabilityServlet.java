package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizers;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class ReadabilityServlet extends MobilizerServlet {

    public ReadabilityServlet() {
        super(Mobilizers.INSTAPAPER_SERVICE_URL);
    }
}
