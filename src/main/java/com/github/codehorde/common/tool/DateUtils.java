package com.github.codehorde.common.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by baomingfeng at 2017-09-04 17:11:08
 */
public class DateUtils {

    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static final String TIME_PATTERN = "HH:mm:ss";

    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String DATETIME_COMPACT_PATTERN = "yyyyMMddHHmmss";

    public static final String DATETIME_MILLI_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    private static ThreadLocal<SimpleDateFormat> dateFormatHolder = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATE_PATTERN);
        }
    };

    private static ThreadLocal<SimpleDateFormat> timeFormatHolder = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(TIME_PATTERN);
        }
    };

    private static ThreadLocal<SimpleDateFormat> datetimeFormatHolder = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATETIME_PATTERN);
        }
    };

    private static ThreadLocal<SimpleDateFormat> compactDatetimeFormatHolder = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATETIME_COMPACT_PATTERN);
        }
    };

    private static ThreadLocal<SimpleDateFormat> datetimeMilliFormatHolder = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat(DATETIME_MILLI_PATTERN);
        }
    };

    public static SimpleDateFormat getDateFormat() {
        return dateFormatHolder.get();
    }

    public static Date parseDate(String text) {
        try {
            return getDateFormat().parse(text);
        } catch (ParseException pe) {
            throw new IllegalArgumentException(pe);
        }
    }

    public static String formatDate(Date date) {
        return getDateFormat().format(date);
    }

    public static SimpleDateFormat getTimeFormat() {
        return timeFormatHolder.get();
    }

    public static Date parseTime(String text) {
        try {
            return getTimeFormat().parse(text);
        } catch (ParseException pe) {
            throw new IllegalArgumentException(pe);
        }
    }

    public static String formatTime(Date date) {
        return getTimeFormat().format(date);
    }

    public static Date parseCompactDatetime(String text) {
        try {
            return getCompactDatetimeFormat().parse(text);
        } catch (ParseException pe) {
            throw new IllegalArgumentException(pe);
        }
    }

    public static SimpleDateFormat getCompactDatetimeFormat() {
        return compactDatetimeFormatHolder.get();
    }

    public static String formatCompactDatetime(Date date) {
        return getCompactDatetimeFormat().format(date);
    }


    public static SimpleDateFormat getDatetimeFormat() {
        return datetimeFormatHolder.get();
    }

    public static Date parseDatetime(String text) {
        try {
            return getDatetimeFormat().parse(text);
        } catch (ParseException pe) {
            throw new IllegalArgumentException(pe);
        }
    }

    public static String formatDatetime(Date date) {
        return getDatetimeFormat().format(date);
    }

    public static SimpleDateFormat getDatetimeMilliFormat() {
        return datetimeMilliFormatHolder.get();
    }

    public static Date parseDatetimeMilli(String text) {
        try {
            return getDatetimeMilliFormat().parse(text);
        } catch (ParseException pe) {
            throw new IllegalArgumentException(pe);
        }
    }

    public static String formatDatetimeMilli(Date date) {
        return getDatetimeMilliFormat().format(date);
    }
}
