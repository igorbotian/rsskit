package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkFeedPhotoTag extends VkFeedItem {

    private static final EntityParser<VkFeedPhotoTag> PARSER = new VkFeedPhotoTagParser();

    public final List<VkPhoto> photoTags;

    public VkFeedPhotoTag(VkFeedItem item, List<VkPhoto> photoTags) {
        super(item);
        this.photoTags = Objects.requireNonNull(photoTags);
    }

    public static VkFeedPhotoTag parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class VkFeedPhotoTagParser extends EntityParser<VkFeedPhotoTag> {

        @Override
        public VkFeedPhotoTag parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            VkFeedItem item = VkFeedItem.parse(json);
            List<VkPhoto> photoTags = parsePhotoTags(getAttribute(json, "photo_tags"));

            return new VkFeedPhotoTag(item, photoTags);
        }

        private List<VkPhoto> parsePhotoTags(JsonNode json) throws RestParseException {
            assert json != null;

            JsonNode items = getAttribute(json, "items");
            List<VkPhoto> photoTags = new ArrayList<>(items.size());

            for(int i = 0 ; i < items.size(); i++) {
                photoTags.add(VkPhoto.parse(items.get(i)));
            }

            return photoTags;
        }
    }
}
