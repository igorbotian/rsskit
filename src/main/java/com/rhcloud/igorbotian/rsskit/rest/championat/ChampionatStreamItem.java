package com.rhcloud.igorbotian.rsskit.rest.championat;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class ChampionatStreamItem {

    public final String id;
    public final Date pubDate;

    public ChampionatStreamItem(String id, Date pubDate) {
        this.id = Objects.requireNonNull(id);
        this.pubDate = Objects.requireNonNull(pubDate);
    }
}
