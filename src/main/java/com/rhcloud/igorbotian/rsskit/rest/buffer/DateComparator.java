package com.rhcloud.igorbotian.rsskit.rest.buffer;

import java.util.Comparator;
import java.util.Date;

/**
 * @author Igor Botian <igor.botian@gmail.com>
 */
class DateComparator implements Comparator<Date> {

    @Override
    public int compare(Date first, Date second) {
        return Long.compare(second.getTime(), first.getTime());
    }
}
