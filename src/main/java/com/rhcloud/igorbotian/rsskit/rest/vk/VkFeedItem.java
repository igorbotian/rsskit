package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkFeedItem {

    private static final VkEntityParser<VkFeedItem> PARSER = new VkFeedItemParser();

    public final VkFeedItemType type;
    public final long sourceID;
    public final Date date;

    public VkFeedItem(VkFeedItem item) {
        Objects.requireNonNull(item);

        this.type = item.type;
        this.sourceID = item.sourceID;
        this.date = item.date;
    }

    public VkFeedItem(VkFeedItemType type, long sourceID, Date date) {
        this.type = Objects.requireNonNull(type);
        this.sourceID = sourceID;
        this.date = Objects.requireNonNull(date);
    }

    public static VkFeedItem parse(JsonNode json) throws VkException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class VkFeedItemParser extends VkEntityParser<VkFeedItem> {

        @Override
        public VkFeedItem parse(JsonNode json) throws VkException {
            Objects.requireNonNull(json);

            VkFeedItemType type = json.has("type")
                    ? VkFeedItemType.parse(json.get("type").asText())
                    : VkFeedItemType.POST; // original post
            long sourceID = json.has("source_id")
                    ? json.get("source_id").asLong()
                    : getAttribute(json, "owner_id").asLong(); // original post
            Date date = new Date(getAttribute(json, "date").asLong() * 1000);

            return new VkFeedItem(type, sourceID, date);
        }
    }
}
