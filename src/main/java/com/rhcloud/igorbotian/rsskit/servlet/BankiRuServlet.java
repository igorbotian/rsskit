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
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class BankiRuServlet extends AbstractRssServlet {

    private static final Logger LOGGER = LogManager.getLogger(BankiRuServlet.class);
    private static final String BANKI_RU_RSS_URL = "http://www.banki.ru/xml/news.rss";
    private static final String BANKS_IDS_PARAM = "ids";
    private static final String BANKS_NAMES_PARAM = "names";

    @Override
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Objects.requireNonNull(req);
        Objects.requireNonNull(resp);

        URL url = new URL(BANKI_RU_RSS_URL);
        SyndFeed rss = downloadRssFeed(url);

        try {
            final BankiRuRssEntryExtractor entryExtractor = new BankiRuRssEntryExtractor(url);
            final Set<String> banksNames = split(req.getParameter(BANKS_NAMES_PARAM), ",");
            final Set<String> banksIds = split(req.getParameter(BANKS_IDS_PARAM), ",");

            RSSUtils.filter(rss, new RssEntryFilter() {

                @Override
                public boolean apply(SyndEntry entry) {
                    String link = entry.getLink();

                    if(entryExtractor.hasBankNameOrId(link)
                            && (banksIds.contains(entryExtractor.getBankId(link))
                            || banksNames.contains(entryExtractor.getBankName(link)))) {
                        return true;
                    }

                    String text = entry.getDescription().getValue();

                    if(StringUtils.isNotEmpty(text)) {
                        text = text.toLowerCase();

                        for (String bankName : banksNames) {
                            if (text.contains(bankName)) {
                                return true;
                            }
                        }
                    }

                    return false;
                }
            });
        } catch (Exception e) {
            LOGGER.error("Failed to extract custom entries from RSS feed", e);
        }

        respond(rss, resp);
    }

    private Set<String> split(String param, String separator) {
        String paramUTF8 = new String(param.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        Set<String> result = new HashSet<>();

        if(StringUtils.isNotEmpty(paramUTF8)) {
            for(String nameOrId : StringUtils.split(paramUTF8.trim(), separator)) {
                result.add(nameOrId.toLowerCase());
            }
        }

        return result;
    }
}
