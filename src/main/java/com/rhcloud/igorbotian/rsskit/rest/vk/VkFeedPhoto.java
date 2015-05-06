package com.rhcloud.igorbotian.rsskit.rest.vk;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class VkFeedPhoto extends VkFeedItem {

    private static final EntityParser<VkFeedPhoto> PARSER = new VkFeedPhotoParser();

    public final long postID;
    public final List<VkPhoto> photos;

    public VkFeedPhoto(VkFeedItem item, long postID, List<VkPhoto> photos) {
        super(item);

        this.postID = postID;
        this.photos = Collections.unmodifiableList(Objects.requireNonNull(photos));
    }

    public static VkFeedPhoto parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class VkFeedPhotoParser extends EntityParser<VkFeedPhoto> {

        @Override
        public VkFeedPhoto parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            VkFeedItem item = VkFeedItem.parse(json);
            long postID = getAttribute(json, "post_id").asLong();
            List<VkPhoto> photos = parsePhotos(getAttribute(json, "photos"));

            return new VkFeedPhoto(item, postID, photos);
        }

        private List<VkPhoto> parsePhotos(JsonNode json) throws RestParseException {
            assert json != null;

            JsonNode items = getAttribute(json, "items");
            List<VkPhoto> photos = new ArrayList<>(items.size());

            for(int i = 0 ; i < items.size(); i++) {
                photos.add(VkPhoto.parse(items.get(i)));
            }

            return photos;
        }
    }
}
