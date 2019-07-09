package com.taboola.multiple_tabs_sdk_api;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;

import com.google.gson.Gson;
import com.taboola.android.api.TaboolaApi;
import com.taboola.android.utils.AssetUtil;

public class SampleApplication extends Application {
    private static final String APP_CONFIG_FILE_TITLE = "app_config.json";
    private AppConfig appConfig;

    @Override
    public void onCreate() {
        super.onCreate();

        String appConfigString = AssetUtil.getHtmlTemplateFileContent(this, APP_CONFIG_FILE_TITLE);
        appConfig = new Gson().fromJson(appConfigString, AppConfig.class);


        Drawable imagePlaceholder = ResourcesCompat.getDrawable(getResources(), R.drawable.image_placeholder, null);

        TaboolaApi.getInstance(appConfig.getPublisher())
                .setImagePlaceholder(imagePlaceholder) // todo set other optional init params if you need to
                .init(getApplicationContext(), appConfig.getApiKey());
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }
}
