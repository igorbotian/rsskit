package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rest.meduza.MeduzaDocument;
import com.rhcloud.igorbotian.rsskit.rest.meduza.MeduzaException;
import com.rhcloud.igorbotian.rsskit.rest.meduza.api.MeduzaAPI;
import com.rhcloud.igorbotian.rsskit.rest.meduza.api.MeduzaAPIImpl;
import com.rhcloud.igorbotian.rsskit.rss.RssGenerator;
import com.rhcloud.igorbotian.rsskit.rss.meduza.MeduzaRssGenerator;
import com.rometools.rome.feed.synd.SyndFeed;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class MeduzaServlet extends AbstractRssServlet {

    private static final Logger LOGGER = LogManager.getLogger(MeduzaServlet.class);
    private static final String LIMIT_PARAM = "limit";
    private static final int LIMIT = 15;
    private final MeduzaAPI api = new MeduzaAPIImpl();
    private final RssGenerator<List<MeduzaDocument>> rssGenerator = new MeduzaRssGenerator();

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);

        try {
            int limit = parseLimit(request);
            List<MeduzaDocument> documents = api.getDocumentsFromIndex(limit);
            SyndFeed rss = rssGenerator.generate(documents);
            respond(rss, response);
        } catch (MeduzaException e) {
            LOGGER.error("Failed to obtain Meduza articles and generate an appropriate RSS feed", e);
            respond(rssGenerator.error(e), response);
        }
    }

    private int parseLimit(HttpServletRequest request) {
        assert request != null;

        String limit = request.getParameter(LIMIT_PARAM);

        if(StringUtils.isNotEmpty(limit)) {
            try {
                return Integer.parseInt(limit);
            } catch (NumberFormatException e) {
                LOGGER.trace("Limit parameter has an invalid value: " + limit, e);
            }
        }

        return LIMIT;
    }
}
