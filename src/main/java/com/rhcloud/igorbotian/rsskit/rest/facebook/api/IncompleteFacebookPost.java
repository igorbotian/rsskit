package com.rhcloud.igorbotian.rsskit.rest.facebook.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookPostType;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookProfile;
import com.rhcloud.igorbotian.rsskit.rest.facebook.json.FacebookProfileParser;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class IncompleteFacebookPost {

    public static final EntityParser<IncompleteFacebookPost> PARSER = new IncompleteFacebookPostParser();

    public final String id;
    public final Date createdTime;
    public final FacebookProfile from;
    public final FacebookPostType type;
    public final String objectID;
    public IncompleteFacebookPost source;

    IncompleteFacebookPost(String id, Date createdTime, FacebookProfile from, FacebookPostType type, String objectID) {

        this.id = Objects.requireNonNull(id);
        this.createdTime = Objects.requireNonNull(createdTime);
        this.from = Objects.requireNonNull(from);
        this.type = Objects.requireNonNull(type);
        this.objectID = objectID;
    }

    private static class IncompleteFacebookPostParser extends EntityParser<IncompleteFacebookPost> {

        private static final FacebookProfileParser FROM_PARSER = new FacebookProfileParser();

        @Override
        public IncompleteFacebookPost parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            String id = getAttribute(json, "id").asText();
            Date createdTime = new Date(1000 * getAttribute(json, "created_time").asLong());
            FacebookProfile from = FROM_PARSER.parse(getAttribute(json, "from"));
            FacebookPostType type = FacebookPostType.parse(getAttribute(json, "type").asText());
            String objectID = json.has("object_id") ? json.get("object_id").asText() : null;

            return new IncompleteFacebookPost(id, createdTime, from, type, objectID);
        }
    }
}
