package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizers;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class InstapaperServlet extends MobilizerServlet {

    public InstapaperServlet() {
        super(Mobilizers.instapaper());
    }
}
