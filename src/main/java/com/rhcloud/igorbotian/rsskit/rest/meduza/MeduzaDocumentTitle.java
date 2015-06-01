package com.rhcloud.igorbotian.rsskit.rest.meduza;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class MeduzaDocumentTitle {

    public final String url;
    public final Date publishedAt;

    public MeduzaDocumentTitle(String url, Date publishedAt) {
        this.url = Objects.requireNonNull(url);
        this.publishedAt = Objects.requireNonNull(publishedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, publishedAt);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof MeduzaDocumentTitle)) {
            return false;
        }

        MeduzaDocumentTitle other = (MeduzaDocumentTitle) obj;
        return StringUtils.equals(url, other.url) && Objects.equals(publishedAt, other.publishedAt);
    }
}
