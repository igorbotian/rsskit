package com.rhcloud.igorbotian.rsskit.rest.championat;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian
 */
public class ChampionatStreamItem {

    public final String id;
    public final Date pubDate;
    public final boolean breaking;
    public final String type;

    public ChampionatStreamItem(String id, Date pubDate, boolean breaking, String type) {
        this.id = Objects.requireNonNull(id);
        this.pubDate = Objects.requireNonNull(pubDate);
        this.breaking = breaking;
        this.type = Objects.requireNonNull(type);
    }
}
