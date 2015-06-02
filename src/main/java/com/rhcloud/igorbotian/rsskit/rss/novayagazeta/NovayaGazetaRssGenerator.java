package com.rhcloud.igorbotian.rsskit.rss.novayagazeta;

import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaArticle;
import com.rhcloud.igorbotian.rsskit.rest.novayagazeta.NovayaGazetaAuthor;
import com.rhcloud.igorbotian.rsskit.rss.RssGenerator;
import com.rometools.rome.feed.synd.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class NovayaGazetaRssGenerator extends RssGenerator<List<NovayaGazetaArticle>>{

    private static final String NOVAYAGAZETA_RU = "http://www.novayagazeta.ru";

    @Override
    protected SyndFeed skeleton() {
        SyndFeed feed = new SyndFeedImpl();

        feed.setTitle("Новая Газета");
        feed.setLink(NOVAYAGAZETA_RU);
        feed.setFeedType(RSS_20);
        feed.setDescription("Журналистские расследования о коррупции в бизнесе и во власти, онлайн-трансляции " +
                "ключевых событий политической и культурной жизни России, специальные репортажи о стране, новости, " +
                "фото-галереи, авторские материалы ведущих журналистов и экспертов.");

        SyndImage image = new SyndImageImpl();
        image.setTitle(feed.getTitle());
        image.setLink(NOVAYAGAZETA_RU);
        image.setUrl(NOVAYAGAZETA_RU + "/i/logo.png");

        feed.setImage(image);

        return feed;
    }

    @Override
    public SyndFeed generate(List<NovayaGazetaArticle> articles) {
        Objects.requireNonNull(articles);

        SyndFeed feed = skeleton();
        feed.setEntries(generateEntries(articles));

        return feed;
    }

    private List<SyndEntry> generateEntries(List<NovayaGazetaArticle> articles) {
        assert articles != null;

        List<SyndEntry> entries = new ArrayList<>(articles.size());

        for(NovayaGazetaArticle article : articles) {
            entries.add(generateEntry(article));
        }

        return entries;
    }

    private SyndEntry generateEntry(NovayaGazetaArticle article) {
        assert article != null;

        SyndEntry entry = new SyndEntryImpl();
        entry.setTitle(article.title);
        entry.setAuthors(generateAuthors(article.authors));
        entry.setLink(article.sourceURL);
        entry.setPublishedDate(article.publishDate);
        entry.setDescription(generateDescription(article));

        if(StringUtils.isNotEmpty(article.imageURL)) {
            SyndEnclosure image = new SyndEnclosureImpl();
            image.setType("image/jpeg");
            image.setUrl(article.imageURL);

            entry.setEnclosures(Collections.singletonList(image));
        }

        return entry;
    }

    private List<SyndPerson> generateAuthors(Set<NovayaGazetaAuthor> authors) {
        assert authors != null;

        List<SyndPerson> persons = new ArrayList<>(authors.size());

        for(NovayaGazetaAuthor author : authors) {
            SyndPerson person = new SyndPersonImpl();
            person.setName(author.name);
        }

        return persons;
    }

    private SyndContent generateDescription(NovayaGazetaArticle article) {
        assert article != null;

        SyndContent description = new SyndContentImpl();
        description.setType(HTML_MIME_TYPE);
        description.setValue(generateDescriptionText(article));

        return description;
    }

    private String generateDescriptionText(NovayaGazetaArticle article) {
        assert article != null;

        StringBuilder text = new StringBuilder();

        if(StringUtils.isNotEmpty(article.description)) {
            text.append(String.format("<i>%s</i>", article.description));
            text.append("<br/>");
        }

        text.append(article.body);

        if(!article.authors.isEmpty()) {
            text.append("<br/>");
            text.append("<i>");
            text.append("Автор");

            if(article.authors.size() > 1) {
                text.append("ы");
            }

            text.append(": ");

            int i = 0;

            for(NovayaGazetaAuthor author : article.authors) {
                if(i > 0) {
                    text.append(", ");
                }

                text.append(author.name);
                i++;
            }

            text.append("</i>");
        }

        return text.toString();
    }
}
