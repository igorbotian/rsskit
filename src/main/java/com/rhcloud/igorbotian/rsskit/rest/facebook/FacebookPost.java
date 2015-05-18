package com.rhcloud.igorbotian.rsskit.rest.facebook;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class FacebookPost implements FacebookObject {

    public enum Type {
        LINK("link"),
        STATUS("status"),
        PHOTO("photo"),
        VIDEO("video"),
        OFFER("offer"),
        UNKNOWN("unknown");

        private String type;

        Type(String type) {
            this.type = type;
        }

        public static Type parse(String type) {
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

    private static final FacebookEntityParser<FacebookPost> PARSER = new FacebookPostParser();

    public final String id;
    public final FacebookAuthor from;
    public final Type type;
    public final FacebookObject object;

    public FacebookPost(String id, FacebookAuthor from, Type type, FacebookObject object) {
        this.id = Objects.requireNonNull(id);
        this.from = Objects.requireNonNull(from);
        this.type = Objects.requireNonNull(type);
        this.object = object;
    }

    public static FacebookPost parse(JsonNode json, FacebookAPI api, String accessToken) throws RestParseException {
        Objects.requireNonNull(json);
        Objects.requireNonNull(api);
        Objects.requireNonNull(accessToken);

        return PARSER.parse(json, api, accessToken);
    }

    private static class FacebookPostParser extends FacebookEntityParser<FacebookPost> {

        private static final Logger LOGGER = LogManager.getLogger(FacebookPostParser.class);

        @Override
        public FacebookPost parse(JsonNode json, FacebookAPI api, String accessToken) throws RestParseException {
            Objects.requireNonNull(json);
            Objects.requireNonNull(api);
            Objects.requireNonNull(accessToken);

            String id = getAttribute(json, "id").asText();
            FacebookAuthor from = json.has("from") ? FacebookAuthor.parse(json.get("from"), api, accessToken) : null;

            Type type;
            FacebookObject object = null;

            if(json.has("type")) {
                type = Type.parse(json.get("type").asText());

                switch (type) {
                    case LINK:
                        object = FacebookLink.parse(json, api, accessToken);
                        break;
                    case STATUS:
                        object = FacebookStatus.parse(json, api, accessToken);
                        break;
                    case VIDEO:
                        object = FacebookVideo.parse(json, api, accessToken);
                        break;
                    case OFFER:
                        object = FacebookOffer.parse(json, api, accessToken);
                        break;
                    case PHOTO:
                        object = FacebookPhoto.parse(json, api, accessToken);
                        break;
                    default:
                        LOGGER.warn("Facebook object type is not recognized" + json.toString());
                }
            } else {
                try {
                    JsonNode objectNode = api.getObject(id, accessToken);

                    if(from == null) {
                        from = FacebookAuthor.parse(getAttribute(objectNode, "from"), api, accessToken);
                    }

                    if(objectNode.has("embed_html")) {
                        type = Type.VIDEO;
                        object = FacebookVideo.parse(objectNode, api, accessToken);
                    } else if(objectNode.has("source")) {
                        type = Type.PHOTO;
                        object = FacebookPhoto.parse(objectNode, api, accessToken);
                    } else {
                        type = Type.UNKNOWN;
                    }

                    if(Type.UNKNOWN.equals(type)) {
                        LOGGER.warn("Unknown object type: " + objectNode.toString());
                    }
                } catch (FacebookException e) {
                    LOGGER.error("Failed to request object with ID = " + id, e);
                    from = null;
                    type = Type.UNKNOWN;
                }
            }

            return new FacebookPost(id, from, type, object);
        }
    }
}
