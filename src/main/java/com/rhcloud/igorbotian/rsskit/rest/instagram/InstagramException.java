package com.rhcloud.igorbotian.rsskit.rest.instagram;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class InstagramException extends Exception {

    public InstagramException() {
        super();
    }

    public InstagramException(String message) {
        super(message);
    }

    public InstagramException(String message, Throwable cause) {
        super(message, cause);
    }

    public InstagramException(Throwable cause) {
        super(cause);
    }

    protected InstagramException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
