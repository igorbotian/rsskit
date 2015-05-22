package com.rhcloud.igorbotian.rsskit.rest.facebook;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public enum FacebookPostType {

    LINK("link"),
    STATUS("status"),
    PHOTO("photo"),
    VIDEO("video"),
    OFFER("offer"),
    UNKNOWN("unknown");

    private String type;

    FacebookPostType(String type) {
        this.type = type;
    }

    public static FacebookPostType parse(String type) {
        Objects.requireNonNull(type);

        if (LINK.type.equals(type)) {
            return LINK;
        } else if (STATUS.type.equals(type)) {
            return STATUS;
        } else if (PHOTO.type.equals(type)) {
            return PHOTO;
        } else if (VIDEO.type.equals(type)) {
            return VIDEO;
        } else if (OFFER.type.equals(type)) {
            return OFFER;
        }

        return UNKNOWN;
    }
}
