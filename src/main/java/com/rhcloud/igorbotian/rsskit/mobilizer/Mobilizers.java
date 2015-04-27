package com.rhcloud.igorbotian.rsskit.mobilizer;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public final class Mobilizers {

    public static final String INSTAPAPER_SERVICE_URL = "https://www.instapaper.com/text?u=";

    private static final Mobilizer instapaper = new InstapaperMobilizer();

    private Mobilizers() {
        //
    }

    public static Mobilizer instapaper() {
        return instapaper;
    }
}
