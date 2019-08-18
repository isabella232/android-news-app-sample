package com.taboola.multiple_tabs_sdk_api;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taboola.android.TaboolaWidget;

import java.util.HashMap;

public class SummaryActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        initView();

        TaboolaWidget taboolaWidget = findViewById(R.id.taboola_view);
        HashMap<String, String> optionalPageCommands = new HashMap<>();
        optionalPageCommands.put("useOnlineTemplate", "true");
        taboolaWidget.setExtraProperties(optionalPageCommands);
        taboolaWidget.fetchContent();
    }

    private void initView() {
        TextView title = findViewById(R.id.tv_summary_title);
        TextView time = findViewById(R.id.tv_summary_time);
        TextView description = findViewById(R.id.tv_summary_description);
        ImageView thumbnail = findViewById(R.id.iv_summary_thumbnail);
        LinearLayout readMore = findViewById(R.id.btn_read_more);

        title.setText(getIntent().getStringExtra("title"));
        time.setText(getIntent().getStringExtra("time"));
        description.setText(getIntent().getStringExtra("description"));

        Picasso.with(this)
                .load(getIntent().getStringExtra("url"))
                .into(thumbnail);

        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // todo send BI event
                String url = getIntent().getStringExtra("clickUrl");
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(SummaryActivity.this, Uri.parse(url));
            }
        });
    }

}