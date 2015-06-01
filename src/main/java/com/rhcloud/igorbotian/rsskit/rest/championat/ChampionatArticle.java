package com.rhcloud.igorbotian.rsskit.rest.championat;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class ChampionatArticle {

    public final String id;
    public final Date pubDate;
    public final String content;
    public final String directLink;
    public final String externalID;
    public final String title;
    public final String type;
    public final String sport;
    public final String imageURL;
    public final String imageCaption;
    public final boolean breaking;

    public ChampionatArticle(String id, Date pubDate, String content, String directLink, String externalID,
                             String title, String type, String sport, String imageURL,
                             String imageCaption, boolean breaking) {

        this.id = Objects.requireNonNull(id);
        this.pubDate = Objects.requireNonNull(pubDate);
        this.content = Objects.requireNonNull(content);
        this.directLink = Objects.requireNonNull(directLink);
        this.externalID = Objects.requireNonNull(externalID);
        this.title = Objects.requireNonNull(title);
        this.type = Objects.requireNonNull(type);
        this.sport = Objects.requireNonNull(sport);
        this.imageURL = Objects.requireNonNull(imageURL);
        this.imageCaption = Objects.requireNonNull(imageCaption);
        this.breaking = breaking;
    }
}
