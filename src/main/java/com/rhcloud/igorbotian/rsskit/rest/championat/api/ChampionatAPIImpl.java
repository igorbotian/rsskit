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
    private static final Comparator<ChampionatArticle> BY_RECENT_PUB_DATE = new Comparator<ChampionatArticle>() {

        @Override
        public int compare(ChampionatArticle first, ChampionatArticle second) {
            return second.pubDate.compareTo(first.pubDate);
        }
    };

    @Override
    public ChampionatStream getStream() throws ChampionatException {
        return stream.getStream();
    }

    @Override
    public ChampionatArticle getArticle(String id) throws ChampionatException {
        Objects.requireNonNull(id);
        return stream.getArticle(id);
    }

    @Override
    public List<ChampionatArticle> getArticles(int limit, boolean breakingOnly) throws ChampionatException {
        if(limit < 1) {
            throw new IllegalArgumentException("Limit should have a positive value: " + limit);
        }

        ChampionatStream stream = getStream();
        List<ChampionatStreamItem> items = filterOnlyNewsAndArticles(new ArrayList<>(stream.items));

        if(breakingOnly) {
            items = filterBreakingOnly(items);
        }

        List<ChampionatArticle> articles = new ArrayList<>(items.size());

        for(ChampionatStreamItem item : items) {
            try {
                articles.add(getArticle(item.id));
            } catch (ChampionatException e) {
                LOGGER.error("Failed to retrieve article content", e);
            }
        }

        Collections.sort(articles, BY_RECENT_PUB_DATE);
        int count = Math.min(limit, items.size());
        return articles.isEmpty() ? articles : articles.subList(0, count);
    }

    private List<ChampionatStreamItem> filterBreakingOnly(List<ChampionatStreamItem> items) {
        assert items != null;

        List<ChampionatStreamItem> filtered = new ArrayList<>();

        for(ChampionatStreamItem item : items) {
            if(item.breaking) {
                filtered.add(item);
            }
        }

        return filtered;
    }

    private List<ChampionatStreamItem> filterOnlyNewsAndArticles(List<ChampionatStreamItem> items) {
        assert items != null;

        List<ChampionatStreamItem> filtered = new ArrayList<>(items.size());

        for(ChampionatStreamItem item : items) {
            if("news".equalsIgnoreCase(item.type) || "article".equals(item.type)) {
                filtered.add(item);
            }
        }

        return filtered;
    }
}
