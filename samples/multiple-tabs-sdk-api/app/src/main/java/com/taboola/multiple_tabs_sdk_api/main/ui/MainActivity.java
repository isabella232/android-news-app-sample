package com.taboola.multiple_tabs_sdk_api.main.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.taboola.android.api.TaboolaApi;
import com.taboola.android.api.TaboolaOnClickListener;
import com.taboola.multiple_tabs_sdk_api.R;
import com.taboola.multiple_tabs_sdk_api.main.SampleApplication;
import com.taboola.multiple_tabs_sdk_api.main.data.AppConfig;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Boolean mForceSummaryPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        AppConfig appConfig = ((SampleApplication) getApplication()).getAppConfig();

        initViewPager(appConfig);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_debug:
                if (mForceSummaryPage == null) {
                    mForceSummaryPage = true;
                } else {
                    mForceSummaryPage = !mForceSummaryPage;
                }
                Snackbar.make(viewPager, mForceSummaryPage ? "DebugMode: open in summary page" : "DebugMode: open in browser", Snackbar.LENGTH_LONG).show();
                return true;


        }

        return super.onOptionsItemSelected(item);
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


                        boolean openInSummaryPage = parseClickUrlForSummaryPage(clickUrl);

                        //this method used for debug and testing purpose only don't add it to your real code
                        if (isForceDebugMode()) {
                            openInSummaryPage = isShowSummaryPage();
                        }
                        //end of debug

                        if (openInSummaryPage) {
                            pagerAdapter.onItemClicked(clickUrl);
                            return false;
                        }

                        return true;
                    }
                });
    }

    private boolean isShowSummaryPage() {
        return mForceSummaryPage;
    }

    private boolean isForceDebugMode() {
        return mForceSummaryPage != null;
    }


    private boolean parseClickUrlForSummaryPage(String clickUrl) {
        Uri uri = Uri.parse(clickUrl);
        String redirUrl = uri.getQueryParameter("redir");
        if (TextUtils.isEmpty(redirUrl)) {
            return false;
        }
        return Uri.parse(redirUrl).getBooleanQueryParameter("summary_page", true);
    }

}
