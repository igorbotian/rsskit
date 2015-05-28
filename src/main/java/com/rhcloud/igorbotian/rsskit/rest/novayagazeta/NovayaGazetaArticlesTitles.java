package com.rhcloud.igorbotian.rsskit.rest.novayagazeta;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class NovayaGazetaArticlesTitles {

    public final List<NovayaGazetaArticleTitle> items;

    public NovayaGazetaArticlesTitles(List<NovayaGazetaArticleTitle> items) {
        this.items = Collections.unmodifiableList(Objects.requireNonNull(items));
    }
}
