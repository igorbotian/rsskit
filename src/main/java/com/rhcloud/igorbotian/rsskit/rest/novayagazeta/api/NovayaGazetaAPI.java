package com.rhcloud.igorbotian.rsskit.rest.novayagazeta.api;

import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaArticle;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaException;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaArticlesTitles;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public interface NovayaGazetaAPI {

    NovayaGazetaArticlesTitles getTopNews(int limit, int offset) throws NovayaGazetaException;
    NovayaGazetaArticlesTitles getEditorsChoice(int limit, int offset) throws NovayaGazetaException;
    NovayaGazetaArticle getArticle(String id) throws NovayaGazetaException;
}
