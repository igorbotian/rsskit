package com.rhcloud.igorbotian.rsskit.rest.vk;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkException extends Exception {

    public VkException() {
        super();
    }

    public VkException(String message) {
        super(message);
    }

    public VkException(String message, Throwable cause) {
        super(message, cause);
    }

    public VkException(Throwable cause) {
        super(cause);
    }

    protected VkException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
