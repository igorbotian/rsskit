package com.rhcloud.igorbotian.rsskit.rest.instagram;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class InstagramPost {

    private static final EntityParser<InstagramPost> PARSER = new InstagramPostParser();

    public final String url;
    public final String thumbnailURL;
    public final String comment;
    public final Date date;
    public final InstagramUser author;

    public InstagramPost(String url, String thumbnailURL, String comment, Date date, InstagramUser author) {
        this.url = Objects.requireNonNull(url);
        this.thumbnailURL = Objects.requireNonNull(thumbnailURL);
        this.comment = Objects.requireNonNull(comment);
        this.date = Objects.requireNonNull(date);
        this.author = Objects.requireNonNull(author);
    }

    public static InstagramPost parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);
        return PARSER.parse(json);
    }

    private static class InstagramPostParser extends EntityParser<InstagramPost> {

        @Override
        public InstagramPost parse(JsonNode json) throws RestParseException {
            Objects.requireNonNull(json);

            InstagramUser user = InstagramUser.parse(getAttribute(json, "user"));
            String comment = json.has("caption") ? parseCaption(json.get("caption")) : "";
            String url = getAttribute(json, "link").asText();
            Date date = new Date(getAttribute(json, "created_time").asLong() * 1000);
            String thumbnailURL = parseThumbnail(getAttribute(json, "images"));

            return new InstagramPost(url, thumbnailURL, comment, date, user);
        }

        private String parseThumbnail(JsonNode images) throws RestParseException {
            assert images != null;

            JsonNode img = getAttribute(images, "standard_resolution");
            return getAttribute(img, "url").asText();
        }

        private String parseCaption(JsonNode caption) throws RestParseException {
            assert caption != null;
            return caption.has("text") ? caption.get("text").asText() : "";
        }
    }
}
