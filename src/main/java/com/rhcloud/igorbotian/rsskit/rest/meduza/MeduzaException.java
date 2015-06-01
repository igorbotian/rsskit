package com.rhcloud.igorbotian.rsskit.rest.meduza;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class MeduzaException extends Exception {

    public MeduzaException() {
        super();
    }

    public MeduzaException(String message) {
        super(message);
    }

    public MeduzaException(String message, Throwable cause) {
        super(message, cause);
    }

    public MeduzaException(Throwable cause) {
        super(cause);
    }

    protected MeduzaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
