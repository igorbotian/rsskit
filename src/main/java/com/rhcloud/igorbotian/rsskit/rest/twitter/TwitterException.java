package com.rhcloud.igorbotian.rsskit.rest.twitter;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterException extends Exception {

    public TwitterException() {
        super();
    }

    public TwitterException(String message) {
        super(message);
    }

    public TwitterException(String message, Throwable cause) {
        super(message, cause);
    }

    public TwitterException(Throwable cause) {
        super(cause);
    }

    protected TwitterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
