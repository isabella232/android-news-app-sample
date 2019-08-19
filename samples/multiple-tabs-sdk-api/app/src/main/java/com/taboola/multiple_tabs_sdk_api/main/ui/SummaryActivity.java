package com.taboola.multiple_tabs_sdk_api.main.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taboola.android.TaboolaWidget;
import com.taboola.android.api.TBRecommendationItem;
import com.taboola.android.utils.AdvertisingIdClient;
import com.taboola.android.utils.OnClickHelper;
import com.taboola.multiple_tabs_sdk_api.R;
import com.taboola.multiple_tabs_sdk_api.main.data.RecommendationItemExtraData;
import com.taboola.multiple_tabs_sdk_api.main.utils.DateTimeUtil;
import com.taboola.multiple_tabs_sdk_api.main.utils.TBDeviceInfoUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SummaryActivity extends Activity {

    public static final String ANDROID = "Android";
    private TBRecommendationItem mTbRecommendationItem;
    private RecommendationItemExtraData mRecommendationItemExtraData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        parseRI(getIntent());
        loadTbItemToHeader();
        initTaboolaFeedWidget();
    }

    private void parseRI(Intent intent) {
        if (intent != null && intent.hasExtra("clickedItem")) {
            mTbRecommendationItem = intent.getParcelableExtra("clickedItem");
            mRecommendationItemExtraData = new RecommendationItemExtraData(mTbRecommendationItem.getExtraDataMap());
        }
    }

    private void initTaboolaFeedWidget() {
        TaboolaWidget taboolaWidget = findViewById(R.id.taboola_view);
        HashMap<String, String> optionalPageCommands = new HashMap<>();
        optionalPageCommands.put("useOnlineTemplate", "true");
        taboolaWidget.setExtraProperties(optionalPageCommands);
        taboolaWidget.fetchContent();
    }

    private void loadTbItemToHeader() {
        this.<TextView>findViewById(R.id.tv_summary_title).setText(mRecommendationItemExtraData.getName());
        String timestamp = DateTimeUtil.getTimeBetween(mRecommendationItemExtraData.getCreated());
        this.<TextView>findViewById(R.id.tv_summary_time).setText(timestamp);
        this.<TextView>findViewById(R.id.tv_summary_branding).setText(mRecommendationItemExtraData.getBranding());
        this.<TextView>findViewById(R.id.tv_summary_description).setText(mRecommendationItemExtraData.getDescription());

        ImageView thumbnail = findViewById(R.id.iv_summary_thumbnail);
        Picasso.with(this)
                .load(getIntent().getStringExtra("url"))
                .into(thumbnail);

        this.<LinearLayout>findViewById(R.id.btn_read_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // todo send BI event
                onReadMoreClicked();
            }
        });


        onTbItemFinishToLoad();


    }

    private void onTbItemFinishToLoad() {
        final String eventType = "SummaryRenderEvent";
        mTbRecommendationItem.reportEvent(eventType, getEventMap(eventType), "youmaylike");
    }

    private Map<String, String> getEventMap(String eventType) {
        Map<String, String> map = new HashMap<>();
        map.put("device_id", AdvertisingIdClient.getCachedAdvertisingId(this));
        map.put("event_type", eventType);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        map.put("time", sdf.format(new Date(System.currentTimeMillis())));
        map.put("os_version", Build.VERSION.RELEASE);
        map.put("os_name", ANDROID);
        map.put("platform", ANDROID);
        map.put("device_manufacturer", Build.MANUFACTURER);
        map.put("language", Locale.getDefault().getDisplayLanguage(Locale.US));
        map.put("publisher", mTbRecommendationItem.getPublisherId());
        map.put("device_model", TBDeviceInfoUtil.getDeviceName());
        map.put("app_version", TBDeviceInfoUtil.getAppVersion(this));
        map.put("carrier", TBDeviceInfoUtil.getCarrier(this));
        map.put("sdk_version", com.taboola.android.BuildConfig.VERSION_NAME);
        return map;
    }


    private void onReadMoreClicked() {
        final String eventType = "ReadMoreClickedEvent";
        mTbRecommendationItem.reportEvent(eventType, getEventMap(eventType), "youmaylike");
        // OnClickHelper.openUrlInTabsOrBrowser(this, mRecommendationItemExtraData.getUrl());

        String url = getIntent().getStringExtra("clickUrl");
        OnClickHelper.openUrlInTabsOrBrowser(this, url);
    }

}