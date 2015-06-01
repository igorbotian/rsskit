package com.rhcloud.igorbotian.rsskit.rest.meduza;

import java.util.*;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class MeduzaIndex {

    public final Set<MeduzaDocumentTitle> documents;

    public MeduzaIndex(Set<MeduzaDocumentTitle> documents) {
        this.documents = Collections.unmodifiableSet(Objects.requireNonNull(documents));
    }
}
