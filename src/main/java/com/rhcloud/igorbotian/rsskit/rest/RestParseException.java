package com.rhcloud.igorbotian.rsskit.rest;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class RestParseException extends Exception {

    public RestParseException() {
        super();
    }

    public RestParseException(String message) {
        super(message);
    }

    public RestParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestParseException(Throwable cause) {
        super(cause);
    }

    protected RestParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
