package com.rhcloud.igorbotian.rsskit.servlet;

import com.rhcloud.igorbotian.rsskit.rss.banki.BankiRuRssEntryExtractor;
import com.rhcloud.igorbotian.rsskit.utils.RSSUtils;
import com.rhcloud.igorbotian.rsskit.utils.RssEntryFilter;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class BankiRuServlet extends AbstractRssServlet {

    private static final Logger LOGGER = LogManager.getLogger(BankiRuServlet.class);
    private static final String BANKI_RU_RSS_URL = "http://www.banki.ru/xml/news.rss";
    private static final String BANKS_PARAM = "banks";

    @Override
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Objects.requireNonNull(req);
        Objects.requireNonNull(resp);

        URL url = new URL(BANKI_RU_RSS_URL);
        SyndFeed rss = downloadRssFeed(url);

        try {
            final BankiRuRssEntryExtractor entryExtractor = new BankiRuRssEntryExtractor(url);
            final Set<String> banksNamesOrIds = extractBanksNamesOrIds(req.getParameter(BANKS_PARAM));

            RSSUtils.filter(rss, new RssEntryFilter() {

                @Override
                public boolean apply(SyndEntry entry) {
                    String link = entry.getLink();

                    return entryExtractor.hasBankNameOrId(link)
                            && (banksNamesOrIds.contains(entryExtractor.getBankId(link))
                            || banksNamesOrIds.contains(entryExtractor.getBankName(link)));

                }
            });
        } catch (Exception e) {
            LOGGER.error("Failed to extract custom entries from RSS feed", e);
        }

        respond(rss, resp);
    }

    private Set<String> extractBanksNamesOrIds(String param) {
        Set<String> namesOrIds = new HashSet<>();

        if(StringUtils.isNotEmpty(param)) {
            Collections.addAll(namesOrIds, StringUtils.split(param.trim(), ","));
        }

        return namesOrIds;
    }
}
