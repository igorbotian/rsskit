package com.rhcloud.igorbotian.rsskit.rest.championat.api;

import com.rhcloud.igorbotian.rsskit.rest.championat.ChampionatArticle;
import com.rhcloud.igorbotian.rsskit.rest.championat.ChampionatException;
import com.rhcloud.igorbotian.rsskit.rest.championat.ChampionatStream;
import com.rhcloud.igorbotian.rsskit.rest.championat.ChampionatStreamItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class ChampionatAPIImpl implements ChampionatAPI {

    private static final Logger LOGGER = LogManager.getLogger(ChampionatAPIImpl.class);
    private static final StreamEndpoint stream = new StreamEndpoint();
    private static final Comparator<ChampionatStreamItem> BY_RECENT_PUB_DATE = new Comparator<ChampionatStreamItem>() {

        @Override
        public int compare(ChampionatStreamItem first, ChampionatStreamItem second) {
            return second.pubDate.compareTo(first.pubDate);
        }
    };

    @Override
    public ChampionatStream getStream(int limit) throws ChampionatException {
        if(limit < 1) {
            throw new IllegalArgumentException("Limit should have a positive value: " + limit);
        }

        return getRecentItems(stream.getStream(), limit);
    }

    private ChampionatStream getRecentItems(ChampionatStream stream, int limit) {
        assert stream != null;
        assert limit > 0;

        List<ChampionatStreamItem> items = new ArrayList<>(stream.items);
        Collections.sort(items, BY_RECENT_PUB_DATE);

        return new ChampionatStream(new LinkedHashSet<>(items.subList(0, Math.min(limit, items.size()))));
    }

    @Override
    public ChampionatArticle getArticle(String id) throws ChampionatException {
        Objects.requireNonNull(id);
        return stream.getArticle(id);
    }

    @Override
    public List<ChampionatArticle> getArticles(int limit) throws ChampionatException {
        if(limit < 1) {
            throw new IllegalArgumentException("Limit should have a positive value: " + limit);
        }

        ChampionatStream stream = getStream(limit);
        List<ChampionatArticle> articles = new ArrayList<>(stream.items.size());

        for(ChampionatStreamItem item : stream.items) {
            try {
                articles.add(getArticle(item.id));
            } catch (ChampionatException e) {
                LOGGER.error("Failed to retrieve article content", e);
            }
        }

        return articles;
    }
}
