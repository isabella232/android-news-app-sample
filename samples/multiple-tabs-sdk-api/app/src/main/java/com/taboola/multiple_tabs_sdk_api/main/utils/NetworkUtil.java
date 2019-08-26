package com.taboola.multiple_tabs_sdk_api.main.utils;

import android.content.Context;
import android.os.Build;

import com.taboola.android.api.TBPlacement;
import com.taboola.android.api.TBRecommendationItem;
import com.taboola.android.utils.AdvertisingIdClient;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NetworkUtil {

    static final String ANDROID = "Android";

    static Map<String, String> createEventMap(Context context, String eventType, TBRecommendationItem recommendationItem) {
        Map<String, String> map = new HashMap<>();
        map.put("event_type", eventType);
        map.put("time", new Timestamp(System.currentTimeMillis()).toString());
        map.put("configVariant", "General");
        map.put("device_id", AdvertisingIdClient.getCachedAdvertisingId(context));
        map.put("os_version", Build.VERSION.RELEASE);
        map.put("os_name", ANDROID);
        map.put("platform", ANDROID);
        map.put("device_manufacturer", Build.MANUFACTURER);
        map.put("language", Locale.getDefault().getDisplayLanguage(Locale.US));
        map.put("publisher", recommendationItem.getPublisherId());
        map.put("device_model", TBDeviceInfoUtil.getDeviceName());
        map.put("app_version", TBDeviceInfoUtil.getAppVersion(context));
        map.put("carrier", TBDeviceInfoUtil.getCarrier(context));
        map.put("sdk_version", com.taboola.android.BuildConfig.VERSION_NAME);
        return map;
    }

    public static void reportMobileEvent(Context context, TBPlacement tbPlacement, TBRecommendationItem tbRecommendationItem, String eventType) {
        final Map<String, String> eventMap = createEventMap(context, eventType, tbRecommendationItem);
        tbPlacement.reportEvent(eventType, eventMap, "youmaylike");

    }
}
