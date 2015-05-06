package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public abstract class VkEntityParser<T> {

    public abstract T parse(JsonNode json) throws VkException;

    protected JsonNode getAttribute(JsonNode parent, String attr) throws VkException {
        assert parent != null;
        assert attr != null;

        if (!parent.has(attr)) {
            throw new VkException("Attribute is expected but not found: " + attr);
        }

        return parent.get(attr);
    }
}
