package com.rhcloud.igorbotian.rsskit.rest.novayagazeta;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class NovayaGazetaArticleTitle {

    public final String id;
    public final String sourceURL;
    public final String description;
    public final String subtitle;
    public final Date publishDate;
    public final String imageURL;
    public final String title;


    public NovayaGazetaArticleTitle(String id, String sourceURL, String description, String subtitle,
                                    Date publishDate, String imageURL, String title) {
        this.id = Objects.requireNonNull(id);
        this.sourceURL = Objects.requireNonNull(sourceURL);
        this.description = Objects.requireNonNull(description);
        this.subtitle = Objects.requireNonNull(subtitle);
        this.publishDate = Objects.requireNonNull(publishDate);
        this.imageURL = Objects.requireNonNull(imageURL);
        this.title = Objects.requireNonNull(title);
    }
}
