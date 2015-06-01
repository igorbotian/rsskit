package com.rhcloud.igorbotian.rsskit.rest.meduza;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class MeduzaDocument {

    public final String url;
    public final String title;
    public final String documentType;
    public final Date publishedAt;
    public final String sourceName;
    public final String sourceURL;
    public final String body;
    public final String description;
    public final String imageURL;
    public final String imageCaption;

    public MeduzaDocument(String url, String title, String documentType, Date publishedAt, String sourceName,
                          String sourceURL, String body, String description, String imageURL, String imageCaption) {

        this.url = Objects.requireNonNull(url);
        this.title = Objects.requireNonNull(title);
        this.documentType = Objects.requireNonNull(documentType);
        this.publishedAt = Objects.requireNonNull(publishedAt);
        this.sourceName = Objects.requireNonNull(sourceName);
        this.sourceURL = Objects.requireNonNull(sourceURL);
        this.body = Objects.requireNonNull(body);
        this.description = Objects.requireNonNull(description);
        this.imageURL = Objects.requireNonNull(imageURL);
        this.imageCaption = Objects.requireNonNull(imageCaption);
    }
}
