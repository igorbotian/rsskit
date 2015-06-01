package com.rhcloud.igorbotian.rsskit.rest.meduza;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author Igor Botian <igor.botyan@alcatel-lucent.com>
 */
public class MeduzaDocumentCategory {

    public final String title;
    public final String screenType;

    public MeduzaDocumentCategory(String title, String screenType) {
        this.title = Objects.requireNonNull(title);
        this.screenType = Objects.requireNonNull(screenType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, screenType);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        } else if (obj == null || !(obj instanceof MeduzaDocumentCategory)) {
            return false;
        }

        MeduzaDocumentCategory other = (MeduzaDocumentCategory) obj;
        return StringUtils.equals(title, other.title) && StringUtils.equals(other.screenType, other.screenType);
    }
}
