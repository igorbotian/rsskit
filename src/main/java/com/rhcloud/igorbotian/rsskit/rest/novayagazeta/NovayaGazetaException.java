package com.rhcloud.igorbotian.rsskit.rest.novayagazeta;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class NovayaGazetaException extends Exception {

    public NovayaGazetaException() {
        super();
    }

    public NovayaGazetaException(String message) {
        super(message);
    }

    public NovayaGazetaException(String message, Throwable cause) {
        super(message, cause);
    }

    public NovayaGazetaException(Throwable cause) {
        super(cause);
    }

    protected NovayaGazetaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
