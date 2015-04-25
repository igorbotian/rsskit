package com.rhcloud.igorbotian.rsskit.servlet;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class ReadabilityServlet extends MobilizerServlet {

    private static final String SERVICE_URL = "http://www.readability.com/read?url=";

    public ReadabilityServlet() {
        super(SERVICE_URL);
    }
}
