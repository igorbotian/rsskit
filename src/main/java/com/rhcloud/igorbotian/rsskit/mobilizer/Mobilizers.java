package com.rhcloud.igorbotian.rsskit.mobilizer;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public final class Mobilizers {

    private static final Mobilizer instapaper = new InstapaperMobilizer();
    private static final Mobilizer mercury = new MercuryMobilizer();

    private Mobilizers() {
        //
    }

    public static Mobilizer instapaper() {
        return instapaper;
    }

    public static Mobilizer mercury() {
        return mercury;
    }
}
