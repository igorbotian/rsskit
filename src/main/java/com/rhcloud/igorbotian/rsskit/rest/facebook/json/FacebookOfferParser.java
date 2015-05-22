package com.rhcloud.igorbotian.rsskit.rest.facebook.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookProfile;
import com.rhcloud.igorbotian.rsskit.rest.facebook.FacebookOffer;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class FacebookOfferParser extends FacebookPostParser<FacebookOffer> {

    @Override
    protected FacebookOffer parse(JsonNode json, String id, Date createdTime, FacebookProfile from,
                                  String caption, String message) throws RestParseException {

        Objects.requireNonNull(json);
        Objects.requireNonNull(id);
        Objects.requireNonNull(createdTime);
        Objects.requireNonNull(from);
        Objects.requireNonNull(caption);
        Objects.requireNonNull(message);

        return new FacebookOffer(id, createdTime, from, caption, message);
    }
}
