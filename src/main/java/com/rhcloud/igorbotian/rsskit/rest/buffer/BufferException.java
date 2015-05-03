package com.rhcloud.igorbotian.rsskit.rest.buffer;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class BufferException extends Exception {

    public BufferException() {
        super();
    }

    public BufferException(String message) {
        super(message);
    }

    public BufferException(String message, Throwable cause) {
        super(message, cause);
    }

    public BufferException(Throwable cause) {
        super(cause);
    }

    protected BufferException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
