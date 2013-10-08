package com.distributedlife.pushflashbang;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

public class JodaDateTime {
    public static void freeze(DateTime frozenDateTime) {
        DateTimeUtils.setCurrentMillisFixed(frozenDateTime.getMillis());
    }

    public static void unfreeze() {
        DateTimeUtils.setCurrentMillisSystem();
    }

}