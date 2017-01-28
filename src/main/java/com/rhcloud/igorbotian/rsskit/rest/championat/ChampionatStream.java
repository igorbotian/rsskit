package com.rhcloud.igorbotian.rsskit.rest.championat;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian
 */
public class ChampionatStream {

    public final Set<ChampionatStreamItem> items;

    public ChampionatStream(Set<ChampionatStreamItem> items) {
        this.items = Collections.unmodifiableSet(Objects.requireNonNull(items));
    }
}
