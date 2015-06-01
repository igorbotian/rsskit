package com.rhcloud.igorbotian.rsskit.rest.meduza.api;

import com.rhcloud.igorbotian.rsskit.rest.meduza.MeduzaDocument;
import com.rhcloud.igorbotian.rsskit.rest.meduza.MeduzaException;
import com.rhcloud.igorbotian.rsskit.rest.meduza.MeduzaIndex;

import java.util.List;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public interface MeduzaAPI {

    MeduzaIndex getIndex() throws MeduzaException;
    MeduzaDocument getDocument(String url) throws MeduzaException;
    List<MeduzaDocument> getDocumentsFromIndex(int limit) throws MeduzaException;
}
