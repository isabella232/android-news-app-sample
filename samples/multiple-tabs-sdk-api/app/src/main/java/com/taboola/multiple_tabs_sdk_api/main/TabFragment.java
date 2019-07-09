package com.taboola.multiple_tabs_sdk_api.main;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.taboola.android.api.TBPlacement;
import com.taboola.multiple_tabs_sdk_api.AppConfig;
import com.taboola.multiple_tabs_sdk_api.R;
import com.taboola.multiple_tabs_sdk_api.SampleApplication;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

public class TabFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = TabFragment.class.getSimpleName();
    private static final String EXTRA_KEY_PLACEMENT_NAME = "PLACEMENT_NAME";
    public static final int NUMBER_OF_ITEMS = 10;

    private AppConfig appConfig;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private FeedAdapter feedAdapter;
    private LinearLayoutManager layoutManager;

    private TBPlacement lastUsedPlacement;

    public static TabFragment newInstance(@NonNull String placementName) {
        Bundle data = new Bundle();
        data.putSerializable(EXTRA_KEY_PLACEMENT_NAME, placementName);

        TabFragment fragment = new TabFragment();
        fragment.setArguments(data);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tab, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.rv_main_category_tab);

        appConfig = ((SampleApplication) getActivity().getApplicationContext()).getAppConfig();

        feedAdapter = new FeedAdapter(getContext());

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(feedAdapter);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.addOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fetchNextPage();
            }
        });

        fetchInitialContent();
        return view;
    }

    private void fetchInitialContent() {
        int thumbnailHeight = (int) getContext().getResources()
                .getDimension(R.dimen.height_feed_article_short_thumbnail);
        int thumbnailWidth = (int) getContext().getResources()
                .getDimension(R.dimen.width_feed_article_short_thumbnail);
        ContentRepository.getFirstContentBatch(appConfig.getPublisher(), getPlacementName(), NUMBER_OF_ITEMS,
                thumbnailWidth, thumbnailHeight, new ContentRepository.ContentFetchCallback() {
                    @Override
                    public void onRecommendationsFetched(TBPlacement placement) {
                        onContentFetched(placement);
                    }

                    @Override
                    public void onRecommendationsFailed(Throwable t) {
                        onContentFetchFailed(t);
                    }
                });
    }

    private void fetchNextPage() {
        ContentRepository.getNextBatchForPlacement(appConfig.getPublisher(), lastUsedPlacement,
                new ContentRepository.ContentFetchCallback() {
                    @Override
                    public void onRecommendationsFetched(TBPlacement placement) {
                        onContentFetched(placement);
                    }

                    @Override
                    public void onRecommendationsFailed(Throwable t) {
                        onContentFetchFailed(t);
                    }
                });
    }

    private void onContentFetched(TBPlacement placement) {
        swipeRefreshLayout.setRefreshing(false);
        lastUsedPlacement = placement;

        List<Object> itemsAsObjectsList = new ArrayList<>(placement.getItems().size());

        itemsAsObjectsList.addAll(placement.getItems());
        itemsAsObjectsList.addAll(ContentRepository.getPlaceholderContent(5));

        feedAdapter.addItems(itemsAsObjectsList);
        feedAdapter.notifyDataSetChanged();
    }

    private void onContentFetchFailed(Throwable t) {
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getActivity(), "Failed to get items: " + t.getMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        feedAdapter.clearItems();
        feedAdapter.notifyDataSetChanged();
        fetchInitialContent();
    }

    @Nullable
    private String getPlacementName() {
        Bundle data = getArguments();
        if (data != null) {
            return data.getString(EXTRA_KEY_PLACEMENT_NAME);
        } else {
            Log.e(TAG, "getPlacementName: placement name is not set");
            return null;
        }
    }
}
