package com.taboola.multiple_tabs_sdk_api.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.taboola.android.api.TaboolaApi;
import com.taboola.android.api.TaboolaOnClickListener;
import com.taboola.multiple_tabs_sdk_api.AppConfig;
import com.taboola.multiple_tabs_sdk_api.R;
import com.taboola.multiple_tabs_sdk_api.SampleApplication;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        AppConfig appConfig = ((SampleApplication) getApplication()).getAppConfig();

        initViewPager(appConfig);
    }

    private void initViewPager(AppConfig appConfig) {
        final MainPagerAdapter pagerAdapter =
                new MainPagerAdapter(getSupportFragmentManager(), appConfig.getTabsConfig());

        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(pagerAdapter);

        if (appConfig.getTabsConfig().size() < 2) {
            tabLayout.setVisibility(View.GONE);
        } else {
            tabLayout.setVisibility(View.VISIBLE);
        }

        TaboolaApi.getInstance(appConfig.getPublisher())
                .setOnClickListener(new TaboolaOnClickListener() {
                    @Override
                    public boolean onItemClick(String placementName, String itemId, String clickUrl, boolean isOrganic) {
                        pagerAdapter.onItemClicked(clickUrl);
                        return false;
                    }
                });
    }

}
