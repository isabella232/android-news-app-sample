package com.taboola.multiple_tabs_sdk_api;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

    private TextView title;
    private TextView time;
    private TextView description;
    private LinearLayout readMore;
    private ImageView page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        TaboolaWidget taboolaWidget = findViewById(R.id.taboola_view);
        HashMap<String, String> optionalPageCommands = new HashMap<>();
        optionalPageCommands.put("useOnlineTemplate", "true");
        taboolaWidget.setExtraProperties(optionalPageCommands);
        taboolaWidget.fetchContent();
        initView();
    }

    private void initView() {
        final Intent intent = getIntent();
        final Context context = this;
        title = findViewById(R.id.item_title);
        title.setText(intent.getStringExtra("title"));
        time = findViewById(R.id.item_time);
        time.setText(intent.getStringExtra("time"));
        description = findViewById(R.id.item_description);
        description.setText(intent.getStringExtra("description"));
        readMore = findViewById(R.id.read_more);
        readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = intent.getStringExtra("clickUrl");
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(context, Uri.parse(url));
            }
        });
        page = findViewById(R.id.item_page);
        Picasso.with(context).load(intent.getStringExtra("url")).into(page);
    }

}