package com.taboola.multiple_tabs_sdk_api;

import java.util.List;

public class AppConfig {
    private String publisher;
    private String apiKey;
    private List<TabConfig> tabsConfig;

    public String getPublisher() {
        return publisher;
    }

    public String getApiKey() {
        return apiKey;
    }

    public List<TabConfig> getTabsConfig() {
        return tabsConfig;
    }

    public class TabConfig {
        private String title;
        private String placement;

        public String getTitle() {
            return title;
        }

        public String getPlacement() {
            return placement;
        }
    }
}