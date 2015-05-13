package com.rhcloud.igorbotian.rsskit.rss.meduza;

import com.rhcloud.igorbotian.rsskit.mobilizer.Mobilizers;
import com.rhcloud.igorbotian.rsskit.rss.RssDescriptionExtender;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class MeduzaRssFeedModifier extends RssDescriptionExtender {

    private static final Logger LOGGER = LogManager.getLogger(MeduzaRssFeedModifier.class);

    @Override
    protected String mobilize(URL url) throws IOException {
        Objects.requireNonNull(url);

        Document htmlDoc = Jsoup.connect(url.toString()).get();
        Elements tags = htmlDoc.select("div[class=DangerousHtml]");

        if (tags.isEmpty()) {
            LOGGER.warn("Failed to mobilize meduza.io page: " + url);
            return Mobilizers.readability().mobilize(url);
        }

        return StringEscapeUtils.unescapeHtml4(tags.get(0).html());
    }
}
