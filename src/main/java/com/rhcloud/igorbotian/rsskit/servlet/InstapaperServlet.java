package com.rhcloud.igorbotian.rsskit.servlet;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class InstapaperServlet extends MobilizerServlet {

    private static final String SERVICE_URL = "https://www.instapaper.com/text?u=";

    public InstapaperServlet() {
        super(SERVICE_URL);
    }
}
