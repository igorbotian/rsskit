package com.rhcloud.igorbotian.rsskit.rest.championat.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.championat.ChampionatStreamItem;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class ChampionatStreamItemParser extends EntityParser<ChampionatStreamItem> {

    @Override
    public ChampionatStreamItem parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);

        String id = getAttribute(json, "_id").asText();
        Date pudDate = new Date(getAttribute(json, "pub_date").asLong() * 1000);
        boolean breaking = "2".equals(getAttribute(getAttribute(getAttribute(json, "visible"), "ru"), "on_sport").asText());
        String type = getAttribute(json, "type").asText();

        return new ChampionatStreamItem(id, pudDate, breaking, type);
    }
}
