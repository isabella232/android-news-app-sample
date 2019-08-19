package com.taboola.multiple_tabs_sdk_api.main.data;

public class FakeItemModel {

    private String title;
    private String imageUrl;

    FakeItemModel(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
