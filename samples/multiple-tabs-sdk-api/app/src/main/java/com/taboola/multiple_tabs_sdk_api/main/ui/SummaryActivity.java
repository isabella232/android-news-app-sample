package com.taboola.multiple_tabs_sdk_api.main.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taboola.android.TaboolaWidget;
import com.taboola.android.api.TBPlacement;
import com.taboola.android.api.TBRecommendationItem;
import com.taboola.android.global_components.eventsmanager.EventType;
import com.taboola.android.utils.OnClickHelper;
import com.taboola.multiple_tabs_sdk_api.R;
import com.taboola.multiple_tabs_sdk_api.main.data.RecommendationItemExtraData;
import com.taboola.multiple_tabs_sdk_api.main.utils.DateTimeUtil;
import com.taboola.multiple_tabs_sdk_api.main.utils.NetworkUtil;

import java.util.HashMap;

public class SummaryActivity extends Activity {

    private TBRecommendationItem mTbRecommendationItem;
    private RecommendationItemExtraData mRecommendationItemExtraData;
    private TBPlacement mPlacemnet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        parseRI(getIntent());
        loadTbItemToHeader();
        initTaboolaFeedWidget();
    }

    private void parseRI(Intent intent) {
        if (intent != null) {
            mTbRecommendationItem = intent.getParcelableExtra("clickedItem");
            mPlacemnet = intent.getParcelableExtra("clickedItemPlacement");
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
        reportMobileEvent(EventType.SUMMARY_RENDER);
    }

    private void onReadMoreClicked() {
        reportMobileEvent(EventType.READ_MORE_CLICK);

        String url = getIntent().getStringExtra("clickUrl");
        OnClickHelper.openUrlInTabsOrBrowser(this, url);
    }

    private void reportMobileEvent(String eventType) {
        NetworkUtil.reportMobileEvent(this, mPlacemnet, mTbRecommendationItem, eventType);
    }

}