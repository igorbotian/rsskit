package com.rhcloud.igorbotian.rsskit.rest.championat;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class ChampionatException extends Exception {

    public ChampionatException() {
        super();
    }

    public ChampionatException(String message) {
        super(message);
    }

    public ChampionatException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChampionatException(Throwable cause) {
        super(cause);
    }

    protected ChampionatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
