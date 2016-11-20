package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizers;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class MercuryServlet extends MobilizerServlet {

    public MercuryServlet() {
        super(Mobilizers.mercury());
    }
}
