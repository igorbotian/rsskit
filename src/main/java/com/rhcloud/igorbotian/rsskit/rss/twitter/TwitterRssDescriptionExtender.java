package com.rhcloud.igorbotian.rsskit.rss.twitter;

import com.rhcloud.igorbotian.rsskit.rss.RssModifier;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEnclosureImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
public class TwitterRssDescriptionExtender implements RssModifier {

    @Override
    public void apply(SyndFeed feed) {
        Objects.requireNonNull(feed);

        for(SyndEntry entry : feed.getEntries()) {
            extendDescription(entry);
        }
    }

    private void extendDescription(SyndEntry entry) {
        assert entry != null;

        List<SyndEnclosure> enclosures = new ArrayList<>(entry.getEnclosures());
        entry.setEnclosures(Collections.<SyndEnclosure>emptyList());

        NameValuePair titleAndLink = extractTitleAndLink(entry.getTitle());
        String title = titleAndLink.getName();
        String url = titleAndLink.getValue();

        if(StringUtils.isNotEmpty(title)) {
            entry.setTitle(title);
        }

        if(StringUtils.isNotEmpty(url)) {
            SyndEnclosure enclosure = new SyndEnclosureImpl();
            enclosure.setUrl(url);
            enclosures.add(0, enclosure);
        }

        entry.getDescription().setType("text/html");
        entry.getDescription().setValue(extendDescription(title, enclosures));
    }

    private NameValuePair extractTitleAndLink(String text) {
        assert text != null;

        String description = text;
        String link = null;

        int pos = text.indexOf("http");

        if (pos >= 0) {
            description = text.substring(0, pos);
            link = (String) new StringTokenizer(text.substring(pos), " ").nextElement();
        }

        return new BasicNameValuePair(description, link);
    }

    private String extendDescription(String text, List<SyndEnclosure> enclosures) {
        assert text != null;
        assert enclosures != null;

        StringBuilder builder = new StringBuilder(text);

        for (SyndEnclosure enclosure : enclosures) {
            if (builder.length() > 0) {
                builder.append("<br/><br/>");
            }

            builder.append(parseAttachment(enclosure.getUrl()));
        }

        return StringEscapeUtils.unescapeHtml4(builder.toString());
    }

    private String parseAttachment(String url) {
        assert url != null;

        String originalURL = url;
        boolean image = false;

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.connect();
            originalURL = getOriginalURL(conn);
            image = isImage(conn);
        } catch (IOException e) {
            // skipping this attachment
        }

        if (image) {
            return String.format("<img src='%s'/>", originalURL);
        }

        return String.format("<a href='%s'/>%s</a>", originalURL, originalURL);
    }

    private String getOriginalURL(HttpURLConnection conn) {
        assert conn != null;

        for (String header : conn.getHeaderFields().keySet()) {
            if ("Location".equals(header)) {
                return conn.getHeaderField(header);
            }
        }

        return conn.getURL().toString();
    }

    private boolean isImage(HttpURLConnection conn) {
        assert conn != null;

        String contentType = conn.getContentType();

        return "image/jpeg".equals(contentType)
                || "image/gif".equals(contentType)
                || "image/png".equals(contentType);
    }
}
