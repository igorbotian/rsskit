package com.rhcloud.igorbotian.rsskit.mobilizer;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public final class Mobilizers {

    private static final Mobilizer instapaper = new InstapaperMobilizer();
    private static final Mobilizer readability = new ReadabilityMobilizer();

    private Mobilizers() {
        //
    }

    public static Mobilizer instapaper() {
        return instapaper;
    }

    public static Mobilizer readability() {
        return readability;
    }
}
