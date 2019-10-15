package com.taboola.multiple_tabs_sdk_api.main.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.TextUtils;

import com.taboola.multiple_tabs_sdk_api.main.data.AppConfig;

import java.util.ArrayList;
import java.util.List;

public class MainPagerAdapter extends FragmentStatePagerAdapter {

    private final List<TabFragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();

    MainPagerAdapter(FragmentManager fm, List<AppConfig.TabConfig> tabsConfig) {
        super(fm);

        for (AppConfig.TabConfig tabConfig : tabsConfig) {
            fragmentList.add(TabFragment.newInstance(tabConfig.getPlacement()));
            fragmentTitleList.add(tabConfig.getTitle());
        }
    }

    public void onItemClicked(@NonNull String clickUrl) {
        for (TabFragment fragment : fragmentList) {
            fragment.onItemClicked(clickUrl);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }

    boolean isOpenInSummaryPage(String placementName, String clickUrl) {
        TabFragment selectedFragment = null;
        for (TabFragment fragment : fragmentList) {
            final String curName = fragment.getPlacementName();
            if (!TextUtils.isEmpty(curName) && placementName.startsWith(curName)) {
                selectedFragment = fragment;
                break;
            }
        }

        if (selectedFragment != null) {
            return selectedFragment.isOpenInSummaryPage(clickUrl);
        }

        return true;
    }
}
