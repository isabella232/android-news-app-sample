package com.taboola.multiple_tabs_sdk_api.main.data;

import java.util.HashMap;

public class RecommendationItemExtraData {

    private static final String DESCRIPTION = "description";
    private static final String NAME = "name";
    private static final String BRANDING = "branding";
    private static final String TYPE = "type";
    private static final String CREATED = "created";

    private String description;
    private String type;
    private String created;
    private String name;
    private String branding;

    public RecommendationItemExtraData(HashMap<String, String> hashMap) {
        description = hashMap.get(DESCRIPTION);
        type = hashMap.get(TYPE);
        created = hashMap.get(CREATED);
        name = hashMap.get(NAME);
        branding = hashMap.get(BRANDING);
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getCreated() {
        return created;
    }

    public String getName() {
        return name;
    }

    public String getBranding() {
        return branding;
    }


}
