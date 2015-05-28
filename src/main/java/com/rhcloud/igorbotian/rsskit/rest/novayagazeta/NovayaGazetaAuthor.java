package com.rhcloud.igorbotian.rsskit.rest.novayagazeta;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class NovayaGazetaAuthor {

    public final String id;
    public final String position;
    public final String name;
    public final String imageURL;

    public NovayaGazetaAuthor(String id, String position, String name, String imageURL) {
        this.id = Objects.requireNonNull(id);
        this.position = Objects.requireNonNull(position);
        this.name = Objects.requireNonNull(name);
        this.imageURL = Objects.requireNonNull(imageURL);
    }
}
