package com.taboola.multiple_tabs_sdk_api.utils;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtil {
    private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";
    private static final String FUTURE_TIMESTAMP_LABEL = "Now";

    @SuppressLint("SimpleDateFormat")
    public static String getTimeBetween(@NonNull String createdTime) {
        String trimmedTime = createdTime.substring(1, createdTime.length() - 1);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        Date date = null;
        try {
            date = simpleDateFormat.parse(trimmedTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        if (System.currentTimeMillis() < date.getTime()) {
            return FUTURE_TIMESTAMP_LABEL;
        } else {
            return (String) DateUtils.getRelativeTimeSpanString(date.getTime()); // TODO localization
        }
    }
}
