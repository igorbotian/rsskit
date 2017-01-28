package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rest.championat.ChampionatArticle;
import com.rhcloud.igorbotian.rsskit.rest.championat.ChampionatException;
import com.rhcloud.igorbotian.rsskit.rest.championat.api.ChampionatAPI;
import com.rhcloud.igorbotian.rsskit.rest.championat.api.ChampionatAPIImpl;
import com.rhcloud.igorbotian.rsskit.rss.RssGenerator;
import com.rhcloud.igorbotian.rsskit.rss.RssModifier;
import com.rhcloud.igorbotian.rsskit.rss.championat.ChampionatRssGenerator;
import com.rhcloud.igorbotian.rsskit.rss.championat.ChampionatRssLinkMobilizer;
import com.rometools.rome.feed.synd.SyndFeed;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian
 */
public class ChampionatServlet extends AbstractRssServlet {

    private static final Logger LOGGER = LogManager.getLogger(ChampionatServlet.class);
    private static final String BREAKING_ONLY_PARAM = "breaking_only";
    private static final String LIMIT_PARAM = "limit";
    private static final int DEFAULT_LIMIT = 15;

    private final ChampionatAPI api = new ChampionatAPIImpl();
    private final RssGenerator<List<ChampionatArticle>> rssGenerator = new ChampionatRssGenerator();
    private final RssModifier linkMobilizer = new ChampionatRssLinkMobilizer();

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        try {
            String breakingOnlyParam = request.getParameter(BREAKING_ONLY_PARAM);
            boolean breakingOnly = Boolean.parseBoolean(breakingOnlyParam) || "on".equalsIgnoreCase(breakingOnlyParam);

            List<ChampionatArticle> articles = api.getArticles(parseLimit(request), breakingOnly);
            SyndFeed rss = rssGenerator.generate(articles);
            linkMobilizer.apply(rss);
            respond(rss, response);
        } catch (ChampionatException e) {
            LOGGER.error("Failed to retrieve championat.com articles and generate an appropriate RSS feed", e);
            respond(rssGenerator.error(e), response);
        }
    }

    private int parseLimit(HttpServletRequest request) {
        assert request != null;

        String limit = request.getParameter(LIMIT_PARAM);

        if (StringUtils.isNotEmpty(limit)) {
            try {
                return Integer.parseInt(limit);
            } catch (NumberFormatException e) {
                LOGGER.warn("Limit parameter should have a positive integer value: " + limit, e);
            }
        }

        return DEFAULT_LIMIT;
    }
}
