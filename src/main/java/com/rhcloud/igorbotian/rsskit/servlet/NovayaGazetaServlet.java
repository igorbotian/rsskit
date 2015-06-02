package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaArticle;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaArticleTitle;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaArticlesTitles;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaException;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.api.NovayaGazetaAPI;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.api.NovayaGazetaAPIImpl;
import com.rhcloud.igorbotian.rsskit.rss.novayagazeta.NovayaGazetaRssGenerator;
import com.rometools.rome.feed.synd.SyndFeed;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class NovayaGazetaServlet extends AbstractRssServlet {

    private static final Logger LOGGER = LogManager.getLogger(NovayaGazetaServlet.class);
    private static final String TOP_NEWS_PARAM = "topnews";
    private static final String LIMIT_PARAM = "limit";
    private static final int LIMIT = 5;
    private final NovayaGazetaAPI api = new NovayaGazetaAPIImpl();
    private final NovayaGazetaRssGenerator rssGenerator = new NovayaGazetaRssGenerator();

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        try {
            int limit = parseLimit(request);
            NovayaGazetaArticlesTitles titles = Boolean.parseBoolean(request.getParameter(TOP_NEWS_PARAM))
                    ? api.getTopNews(limit, 0) : api.getEditorsChoice(limit, 0);
            List<NovayaGazetaArticle> articles = downloadArticles(titles);
            SyndFeed rss = rssGenerator.generate(articles);

            respond(rss, response);
        } catch (NovayaGazetaException e) {
            LOGGER.error("Failed to get Novaya Gazeta articles and generate an appropriate RSS feed", e);
            respond(rssGenerator.error(e), response);
        }
    }

    private int parseLimit(HttpServletRequest request) {
        assert request != null;

        String limit = request.getParameter(LIMIT_PARAM);

        try {
            return StringUtils.isNotEmpty(limit) ? Integer.parseInt(limit) : LIMIT;
        } catch (NumberFormatException e) {
            return LIMIT;
        }
    }

    private List<NovayaGazetaArticle> downloadArticles(NovayaGazetaArticlesTitles titles) throws NovayaGazetaException {
        assert titles != null;

        List<NovayaGazetaArticle> articles = new ArrayList<>(titles.items.size());

        for(NovayaGazetaArticleTitle title : titles.items) {
            try {
                articles.add(api.getArticle(title.id));
            } catch (NovayaGazetaException e) {
                LOGGER.error("Failed to download an article specified by URL: " + title.sourceURL, e);
            }
        }

        return articles;
    }
}
