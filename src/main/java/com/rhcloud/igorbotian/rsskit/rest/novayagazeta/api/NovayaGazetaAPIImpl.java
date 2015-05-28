package com.rhcloud.igorbotian.rsskit.rest.novayagazeta.api;

import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaArticle;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaArticlesTitles;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaException;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class NovayaGazetaAPIImpl implements NovayaGazetaAPI {

    private static final int EDITORS_CHOICE_RUBRIC_ID = 999;
    private static final ArticlesEndpoint topNews = new ArticlesEndpoint("topnews/");
    private static final ArticlesEndpoint articles = new ArticlesEndpoint("articles/");
    private static final ArticleEndpoint article = new ArticleEndpoint();

    @Override
    public NovayaGazetaArticlesTitles getTopNews(int limit, int offset) throws NovayaGazetaException {
        if (limit < 1) {
            throw new IllegalArgumentException("Limit should have a positive value: " + limit);
        }

        if (offset < 0) {
            throw new IllegalArgumentException("Offset should have a positive value: " + offset);
        }

        return topNews.getTitles(limit, offset);
    }

    @Override
    public NovayaGazetaArticlesTitles getEditorsChoice(int limit, int offset) throws NovayaGazetaException {
        if (limit < 1) {
            throw new IllegalArgumentException("Limit should have a positive value: " + limit);
        }

        if (offset < 0) {
            throw new IllegalArgumentException("Offset should have a positive value: " + offset);
        }

        Set<NameValuePair> otherParams = new HashSet<>();
        otherParams.add(new BasicNameValuePair("rubric_id", Integer.toString(EDITORS_CHOICE_RUBRIC_ID)));

        return articles.getTitles(limit, offset, otherParams);
    }

    @Override
    public NovayaGazetaArticle getArticle(String id) throws NovayaGazetaException {
        Objects.requireNonNull(id);
        return article.get(id);
    }
}
