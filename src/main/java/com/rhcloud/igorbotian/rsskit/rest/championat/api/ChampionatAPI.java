package com.rhcloud.igorbotian.rsskit.rest.championat.api;

import com.rhcloud.igorbotian.rsskit.rest.championat.ChampionatArticle;
import com.rhcloud.igorbotian.rsskit.rest.championat.ChampionatException;
import com.rhcloud.igorbotian.rsskit.rest.championat.ChampionatStream;

import java.util.List;

/**
 * @author Igor Botian
 */
public interface ChampionatAPI {

    ChampionatStream getStream() throws ChampionatException;

    ChampionatArticle getArticle(String id) throws ChampionatException;

    List<ChampionatArticle> getArticles(int limit, boolean breakingOnly) throws ChampionatException;
}
