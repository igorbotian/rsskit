package com.rhcloud.igorbotian.rsskit.mobilizer;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class InstapaperMobilizer implements Mobilizer {

    private static final String SERVICE_URL = "https://www.instapaper.com/text?u=";
    private static final String[] SUPPORTED_PROTOCOLS = {"http", "https"};

    @Override
    public String mobilize(URL url) throws IOException {
        Objects.requireNonNull(url);

        URL mobilizedURL = mobilizeURL(url);
        return downloadPage(mobilizedURL);
    }

    private URL mobilizeURL(URL url) throws IOException {
        assert url != null;

        return new URL(SERVICE_URL + url.toString());
    }

    private String downloadPage(URL url) throws IOException {
        assert url != null;

        checkProtocolSupported(url);
        return downloadContents(url);
    }

    private void checkProtocolSupported(URL url) throws IOException {
        assert url != null;

        for (String protocol : SUPPORTED_PROTOCOLS) {
            if (url.toString().startsWith(protocol)) {
                return;
            }
        }

        throw new IOException("Protocol is not supported for mobilizing: " + url.toString());
    }

    private String downloadContents(URL link) throws IOException {
        assert link != null;

        Connection conn = Jsoup.connect(link.toString());
        Document htmlDoc = conn.get();
        htmlDoc.outputSettings().charset(StandardCharsets.UTF_8);
        htmlDoc.outputSettings().escapeMode(Entities.EscapeMode.base);

        return StringEscapeUtils.unescapeHtml4(htmlDoc.getElementsByTag("main").html());
    }
}
