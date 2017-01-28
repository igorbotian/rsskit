package com.rhcloud.igorbotian.rsskit.rest.facebook;

/**
 * @author Igor Botian
 */
public class FacebookException extends Exception {

    public FacebookException() {
        super();
    }

    public FacebookException(String message) {
        super(message);
    }

    public FacebookException(String message, Throwable cause) {
        super(message, cause);
    }

    public FacebookException(Throwable cause) {
        super(cause);
    }

    protected FacebookException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
