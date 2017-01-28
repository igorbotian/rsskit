package com.rhcloud.igorbotian.rsskit.rss.championat;

import com.rhcloud.igorbotian.rsskit.rest.championat.ChampionatArticle;
import com.rhcloud.igorbotian.rsskit.rss.RssGenerator;
import com.rometools.rome.feed.synd.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author Igor Botian
 */
public class ChampionatRssGenerator extends RssGenerator<List<ChampionatArticle>> {

    public static final String BREAKING_CATEGORY = "breaking";
    public static final String CHAMPIONAT_COM = "http://www.championat.com";

    @Override
    protected SyndFeed skeleton() {
        SyndFeed feed = new SyndFeedImpl();

        feed.setPublishedDate(new Date());
        feed.setLink(CHAMPIONAT_COM);
        feed.setTitle("Чемпионат.com");
        feed.setFeedType(RSS_20);
        feed.setDescription(feed.getTitle());

        SyndImage image = new SyndImageImpl();
        image.setTitle(feed.getTitle());
        image.setUrl("http://st.championat.net/i/rss-logo.png");
        feed.setImage(image);

        return feed;
    }

    @Override
    public SyndFeed generate(List<ChampionatArticle> articles) {
        Objects.requireNonNull(articles);

        List<SyndEntry> entries = generateEntries(articles);
        SyndFeed feed = skeleton();
        feed.setEntries(entries);

        if (!entries.isEmpty()) {
            feed.setPublishedDate(entries.get(entries.size() - 1).getPublishedDate());
        }

        return feed;
    }

    private List<SyndEntry> generateEntries(List<ChampionatArticle> articles) {
        assert articles != null;

        List<SyndEntry> entries = new ArrayList<>(articles.size());

        for (ChampionatArticle article : articles) {
            entries.add(generateEntry(article));
        }

        return entries;
    }

    private SyndEntry generateEntry(ChampionatArticle article) {
        assert article != null;

        SyndEntry entry = new SyndEntryImpl();

        entry.setLink(CHAMPIONAT_COM + article.directLink);
        entry.setPublishedDate(article.pubDate);
        entry.setTitle(article.title);
        entry.setDescription(generateDescription(article));

        if(article.breaking) {
            SyndCategory category = new SyndCategoryImpl();
            category.setName(BREAKING_CATEGORY);
            entry.setCategories(Collections.singletonList(category));
        }

        return entry;
    }

    private SyndContent generateDescription(ChampionatArticle article) {
        assert article != null;

        SyndContent description = new SyndContentImpl();
        description.setValue(generateDescriptionText(article));
        description.setType(HTML_MIME_TYPE);

        return description;
    }

    private String generateDescriptionText(ChampionatArticle article) {
        assert article != null;

        StringBuilder description = new StringBuilder();

        if(StringUtils.isNotEmpty(article.imageURL)) {
            description.append(String.format("<img src='http://img.championat.com/%s'/>", article.imageURL));

            if(StringUtils.isNotEmpty(article.imageCaption)) {
                description.append("<br/>");
                description.append(article.imageCaption);
            }
        }

        if(description.length() > 0) {
            description.append("<br/><br/>");
        }

        description.append(article.content);

        return description.toString();
    }
}
