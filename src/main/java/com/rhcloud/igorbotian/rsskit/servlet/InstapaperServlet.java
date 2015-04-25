package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.proxy.Mobilizers;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class InstapaperServlet extends MobilizerServlet {

    public InstapaperServlet() {
        super(Mobilizers.INSTAPAPER_SERVICE_URL);
    }
}
