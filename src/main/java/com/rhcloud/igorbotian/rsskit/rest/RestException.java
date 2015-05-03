package com.rhcloud.igorbotian.rsskit.rest;

import java.io.IOException;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class RestException extends IOException {

    public RestException() {
        super();
    }

    public RestException(String message) {
        super(message);
    }

    public RestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestException(Throwable cause) {
        super(cause);
    }
}
