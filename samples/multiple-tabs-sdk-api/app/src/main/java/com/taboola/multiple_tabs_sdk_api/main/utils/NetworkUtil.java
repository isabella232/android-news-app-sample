package com.taboola.multiple_tabs_sdk_api.main.utils;

import android.content.Context;

import com.taboola.android.api.TBPlacement;
import com.taboola.android.api.TBRecommendationItem;

import java.util.HashMap;
import java.util.Map;

public class NetworkUtil {

    static final String ANDROID = "Android";

    static Map<String, String> createEventMap(Context context, String eventType, TBRecommendationItem recommendationItem) {
        Map<String, String> map = new HashMap<>();
        map.put("configVariant", "General");
        return map;
    }

    public static void reportMobileEvent(Context context, TBPlacement tbPlacement, TBRecommendationItem tbRecommendationItem, String eventType) {
        final Map<String, String> eventMap = createEventMap(context, eventType, tbRecommendationItem);
        tbPlacement.reportEvent(eventType, eventMap, "youmaylike");

    }
}
