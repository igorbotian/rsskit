package com.rhcloud.igorbotian.rsskit.rest.meduza.api;

import com.rhcloud.igorbotian.rsskit.rest.meduza.MeduzaDocument;
import com.rhcloud.igorbotian.rsskit.rest.meduza.MeduzaDocumentTitle;
import com.rhcloud.igorbotian.rsskit.rest.meduza.MeduzaException;
import com.rhcloud.igorbotian.rsskit.rest.meduza.MeduzaIndex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class MeduzaAPIImpl implements MeduzaAPI {

    private static final IndexEndpoint index = new IndexEndpoint();
    private static final DocumentEndpoint documents = new DocumentEndpoint();
    private static final Comparator<MeduzaDocumentTitle> BY_DATE = new Comparator<MeduzaDocumentTitle>() {

        @Override
        public int compare(MeduzaDocumentTitle first, MeduzaDocumentTitle second) {
            return second.publishedAt.compareTo(first.publishedAt);
        }
    };

    @Override
    public MeduzaIndex getIndex() throws MeduzaException {
        return index.get();
    }

    @Override
    public MeduzaDocument getDocument(String url) throws MeduzaException {
        return documents.get(url);
    }

    @Override
    public List<MeduzaDocument> getDocumentsFromIndex(int limit) throws MeduzaException {
        if (limit < 1) {
            throw new MeduzaException("Limit should have a positive value: " + limit);
        }

        List<MeduzaDocument> documents = new ArrayList<>();
        List<MeduzaDocumentTitle> titles = new ArrayList<>(getIndex().documents);
        int count = Math.min(limit, titles.size());
        Collections.sort(titles, BY_DATE);

        for (MeduzaDocumentTitle title : titles.subList(0, count)) {
            documents.add(MeduzaAPIImpl.documents.get(title.url));
        }

        return documents;
    }
}
