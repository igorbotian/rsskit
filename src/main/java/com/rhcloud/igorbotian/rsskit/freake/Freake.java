package com.rhcloud.igorbotian.rsskit.freake;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class Freake {

    private static final String FREAKE_RU = "http://www.freake.ru";

    private final URL url;
    private final URL rssURL;
    private final URL faviconURL;

    public Freake() throws IOException {
        this.url = initURL();
        this.rssURL = initRssURL();
        this.faviconURL = initFaviconURL();
    }

    private URL initURL() throws MalformedURLException {
        return new URL(FREAKE_RU);
    }

    public URL url() {
        return url;
    }

    private URL initRssURL() throws IOException {
        URL url = url();
        Document htmlDoc = Jsoup.connect(url.toString()).get();
        Elements links = htmlDoc.select("link[type]");

        for (Element link : links) {
            Attributes attributes = link.attributes();
            String rel = attributes.get("rel");

            if ("alternate".equals(rel)) {
                String type = attributes.get("type");
                assert type != null;

                if ("application/rss+xml".equals(type)) {
                    String href = attributes.get("href");

                    if(StringUtils.isNotEmpty(href)) {
                        return makeAbsolute(url, href);
                    }
                }
            }
        }

        return new URL(url.getProtocol(), url.getHost(), url.getPort(), "/rss");
    }

    private URL makeAbsolute(URL baseURL, String file) throws MalformedURLException {
        assert baseURL != null;
        assert file != null;

        if (file.startsWith("http")) {
            return new URL(file);
        }

        int startPos = 0;

        if (file.startsWith("//")) {
            startPos = 2;
        } else if (file.startsWith("/")) {
            startPos = 1;
        }

        return new URL(baseURL.getProtocol(), baseURL.getHost(), baseURL.getPort(), "/" + file.substring(startPos));
    }

    public URL rssURL() {
        return rssURL;
    }

    private URL initFaviconURL() throws IOException {
        URL url = url();
        Document htmlDoc = Jsoup.connect(url.toString()).get();
        Elements links = htmlDoc.select("link[href]");

        for (Element link : links) {
            Attributes attributes = link.attributes();
            String rel = attributes.get("rel");

            if ("shortcut icon".equals(rel)) {
                String href = attributes.get("href");
                assert href != null;

                return makeAbsolute(url, href);
            }
        }

        return new URL(url.getProtocol(), url.getHost(), url.getPort(), "/favicon.ico");
    }

    public URL faviconURL() {
        return faviconURL;
    }
}
