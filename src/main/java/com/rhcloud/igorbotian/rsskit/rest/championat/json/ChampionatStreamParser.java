package com.rhcloud.igorbotian.rsskit.rest.championat.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.rhcloud.igorbotian.rsskit.rest.EntityParser;
import com.rhcloud.igorbotian.rsskit.rest.RestParseException;
import com.rhcloud.igorbotian.rsskit.rest.championat.ChampionatStream;
import com.rhcloud.igorbotian.rsskit.rest.championat.ChampionatStreamItem;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class ChampionatStreamParser extends EntityParser<ChampionatStream> {

    private static final EntityParser<ChampionatStreamItem> ITEM_PARSER = new ChampionatStreamItemParser();

    @Override
    public ChampionatStream parse(JsonNode json) throws RestParseException {
        Objects.requireNonNull(json);

        Set<ChampionatStreamItem> items = new HashSet<>();

        for(int i = 0; i < json.size(); i++) {
            items.add(ITEM_PARSER.parse(json.get(i)));
        }

        return new ChampionatStream(items);
    }
}
