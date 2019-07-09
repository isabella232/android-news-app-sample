package com.taboola.multiple_tabs_sdk_api.main;

import com.taboola.android.api.TBPlacement;
import com.taboola.android.api.TBPlacementRequest;
import com.taboola.android.api.TBRecommendationRequestCallback;
import com.taboola.android.api.TBRecommendationsRequest;
import com.taboola.android.api.TBRecommendationsResponse;
import com.taboola.android.api.TaboolaApi;

import java.util.ArrayList;
import java.util.List;

public class ContentRepository {

    public interface ContentFetchCallback {
        void onRecommendationsFetched(TBPlacement placement);

        void onRecommendationsFailed(Throwable t);
    }

    public static void getFirstContentBatch(String publisher,
                                            final String placementName,
                                            int numberOfItems,
                                            int imageWidth,
                                            int imageHeight,
                                            final ContentFetchCallback callback) {
        TBRecommendationsRequest request =
                new TBRecommendationsRequest("http://example.com", "text"); // todo set your parameters

        TBPlacementRequest placementRequest =
                new TBPlacementRequest(placementName, numberOfItems)
                        .setAvailable(true)
                        .setThumbnailSize(imageWidth, imageHeight);
        request.addPlacementRequest(placementRequest);

        TaboolaApi.getInstance(publisher)
                .fetchRecommendations(request, new TBRecommendationRequestCallback() {
                    @Override
                    public void onRecommendationsFetched(TBRecommendationsResponse response) {
                        TBPlacement firstPlacement = response.getPlacementsMap().get(placementName);
                        callback.onRecommendationsFetched(firstPlacement);
                    }

                    @Override
                    public void onRecommendationsFailed(Throwable t) {
                        callback.onRecommendationsFailed(t);
                    }
                });
    }

    public static void getNextBatchForPlacement(String publisher,
                                                TBPlacement lastUsedPlacement,
                                                final ContentFetchCallback callback) {
        TaboolaApi.getInstance(publisher)
                .getNextBatchForPlacement(lastUsedPlacement, new TBRecommendationRequestCallback() {
                    @Override
                    public void onRecommendationsFetched(TBRecommendationsResponse response) {
                        // starting from second batch, placement names will have a counter added
                        TBPlacement nextPlacement = response.getPlacementsMap().values().iterator().next();
                        callback.onRecommendationsFetched(nextPlacement);
                    }

                    @Override
                    public void onRecommendationsFailed(Throwable t) {
                        callback.onRecommendationsFailed(t);
                    }
                });
    }

    public static List<FakeItemModel> getPlaceholderContent(int numberOfItems) {
        List<FakeItemModel> list = new ArrayList<>();
        for (int i = 0; i < numberOfItems; i++) {
            list.add(new FakeItemModel("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod " +
                    "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                    "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
                    ""));
        }
        return list;
    }

}
