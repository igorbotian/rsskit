package com.rhcloud.igorbotian.rsskit.rest.vk;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public enum VkFeedItemType {

    POST("post"),
    PHOTO("photo"),
    PHOTO_TAG("photo_tag"),
    WALL_PHOTO("wall_photo"),
    FRIEND("friend"),
    NOTE("note"),
    UNKNOWN("unknown");

    private String str;

    VkFeedItemType(String str) {
        this.str = str;
    }

    public static VkFeedItemType parse(String str) {
        Objects.requireNonNull(str);

        for (VkFeedItemType type : values()) {
            if (type.str.equals(str)) {
                return type;
            }
        }

        return UNKNOWN;
    }

    @Override
    public String toString() {
        return str;
    }
}
