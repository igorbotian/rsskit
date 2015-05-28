package com.rhcloud.igorbotian.rsskit.rest.novayagazeta;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class NovayaGazetaArticle {

    public final String id;
    public final Set<NovayaGazetaAuthor> authors;
    public final String body;
    public final String sourceURL;
    public final String description;
    public final String subtitle;
    public final Date publishDate;
    public final String imageURL;
    public final String title;

    public NovayaGazetaArticle(String id, Set<NovayaGazetaAuthor> authors, String body, String sourceURL,
                               String description, String subtitle, Date publishDate, String imageURL, String title) {
        this.id = Objects.requireNonNull(id);
        this.authors = Collections.unmodifiableSet(Objects.requireNonNull(authors));
        this.body = Objects.requireNonNull(body);
        this.sourceURL = Objects.requireNonNull(sourceURL);
        this.description = Objects.requireNonNull(description);
        this.subtitle = Objects.requireNonNull(subtitle);
        this.publishDate = Objects.requireNonNull(publishDate);
        this.imageURL = Objects.requireNonNull(imageURL);
        this.title = Objects.requireNonNull(title);
    }
}
