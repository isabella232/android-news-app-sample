package com.taboola.multiple_tabs_sdk_api.main.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.taboola.android.api.TBImageView;
import com.taboola.android.api.TBRecommendationItem;
import com.taboola.android.api.TBTextView;
import com.taboola.multiple_tabs_sdk_api.R;
import com.taboola.multiple_tabs_sdk_api.main.data.FakeItemModel;
import com.taboola.multiple_tabs_sdk_api.main.utils.DateTimeUtil;
import com.taboola.multiple_tabs_sdk_api.main.utils.ItemUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TYPE_SHORT_ITEM = 0;
    private final int TYPE_FAKE_ITEM = 1;

    private List<Object> feedItems = new ArrayList<>();
    private Map<String, TBRecommendationItem> mRecommendationItemMap = new HashMap<>();

    private Context context;

    FeedAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_SHORT_ITEM: {
                ConstraintLayout shortItemLayout = (ConstraintLayout) inflater
                        .inflate(R.layout.item_article_short, parent, false);
                return new ShortItemViewHolder(shortItemLayout);
            }
            case TYPE_FAKE_ITEM: {
                ConstraintLayout fakeItemLayout = (ConstraintLayout) inflater
                        .inflate(R.layout.item_fake, parent, false);
                return new FakeItemViewHolder(fakeItemLayout);
            }
            default: {
                throw new IllegalStateException("Unknown view type: " + viewType);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ShortItemViewHolder) {
            ((ShortItemViewHolder) viewHolder).bind((TBRecommendationItem) feedItems.get(position));
        } else if (viewHolder instanceof FakeItemViewHolder) {
            ((FakeItemViewHolder) viewHolder).bind((FakeItemModel) feedItems.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (feedItems.get(position) instanceof TBRecommendationItem) {
            return TYPE_SHORT_ITEM;
        } else if (feedItems.get(position) instanceof FakeItemModel) {
            return TYPE_FAKE_ITEM;
        } else {
            throw new RuntimeException("Unknown item type");
        }
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ShortItemViewHolder) {
            ((ShortItemViewHolder) holder).onViewRecycled();
        }
    }

    void addItems(List<Object> newItems) {
        feedItems.addAll(newItems);
        addItemsToMap(newItems);

    }

    private void addItemsToMap(List<Object> newItems) {
        //using this map to find item O(1)
        for (Object feedItem : newItems) {
            if (feedItem instanceof TBRecommendationItem) {
                TBRecommendationItem tbRecommendationItem = (TBRecommendationItem) feedItem;
                String currentItemUrl = (tbRecommendationItem).getExtraDataMap().get("url");
                if (!TextUtils.isEmpty(currentItemUrl)) {
                    mRecommendationItemMap.put(currentItemUrl, tbRecommendationItem);
                }
            }
        }
    }

    @Nullable
    private TBRecommendationItem findItemByClickUrl(String clickUrl) {

        // you can also avoid use map and instead for over all items and look for matching item clickUrl and currentItemUrl (see addItemsToMap method)
        return mRecommendationItemMap.get(clickUrl);
    }

    void clearItems() {
        feedItems.clear();
    }

    void onItemClicked(@NonNull final String clickUrl) {
        TBRecommendationItem clickedItem = findItemByClickUrl(clickUrl);
        if (clickedItem == null) {
            return; // item that was clicked is not in this fragment
        }

        Bundle bundle = new Bundle();
        int imageHeight = (int) context.getResources().getDimension(R.dimen.summary_page_thumbnail_height);
        bundle.putString("url", ItemUtil.getItemThumbnailUrl(clickedItem, ItemUtil.getScreenWidth(), imageHeight));
        bundle.putString("clickUrl", clickUrl);
        bundle.putParcelable("clickedItem", clickedItem);
        bundle.putParcelable("clickedItemPlacement", clickedItem.getPlacement());

        Intent intent = new Intent(context, SummaryActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }




    boolean isSummaryPage(String clickUrl) {
        TBRecommendationItem clickedItem = findItemByClickUrl(clickUrl);
        if (clickedItem == null) {
            return false;
        }

        try {
            final String custom_data = clickedItem.getExtraDataMap().get("custom_data");
            if (!TextUtils.isEmpty(custom_data)) {
                return new JSONObject(custom_data).optBoolean("is_summary", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //default is to open in summary page anyway
        return true;
    }

    class ShortItemViewHolder extends RecyclerView.ViewHolder {

        private FrameLayout itemTitleContainer;
        private FrameLayout itemImageContainer;
        private FrameLayout itemBrandingContainer;
        private TextView itemTime;

        ShortItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitleContainer = itemView.findViewById(R.id.container_title);
            itemImageContainer = itemView.findViewById(R.id.container_image);
            itemBrandingContainer = itemView.findViewById(R.id.container_branding);
            itemTime = itemView.findViewById(R.id.tv_time);
        }

        void bind(TBRecommendationItem item) {
            itemTitleContainer.removeAllViews();
            itemImageContainer.removeAllViews();
            itemBrandingContainer.removeAllViews();

            int thumbnailHeight = (int) context.getResources().getDimension(R.dimen.height_feed_article_short_thumbnail);
            int thumbnailWidth = (int) context.getResources().getDimension(R.dimen.width_feed_article_short_thumbnail);

            // customize the look of Taboola views to fit your app's theme
            final TBImageView thumbnail = item.getThumbnailView(context, thumbnailHeight, thumbnailWidth);
            thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);

            TBTextView title = item.getTitleView(context);
            title.setMaxLines(3);
            title.setEllipsize(TextUtils.TruncateAt.END);

            TBTextView brandingLabel = item.getBrandingView(context);
            if (brandingLabel != null) {
                brandingLabel.setMaxLines(1);
                brandingLabel.setLayoutParams(new FrameLayout.LayoutParams
                        (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                brandingLabel.setEllipsize(TextUtils.TruncateAt.END);
                brandingLabel.setGravity(Gravity.CENTER);
            }

            final HashMap<String, String> extraDataMap = item.getExtraDataMap();
            final String created = extraDataMap.get("created");
            if (created != null) {
                String timestamp = DateTimeUtil.getTimeBetween(created);
                itemTime.setText(timestamp);
            }

            // remove our views from previous containers if necessary
            if (thumbnail.getParent() != null) {
                ((ViewGroup) thumbnail.getParent()).removeView(thumbnail);
            }
            itemImageContainer.addView(thumbnail);

            if (title.getParent() != null) {
                ((ViewGroup) title.getParent()).removeView(title);
            }
            itemTitleContainer.addView(title);

            if (brandingLabel != null && brandingLabel.getParent() != null) {
                ((ViewGroup) brandingLabel.getParent()).removeView(brandingLabel);
            }
            itemBrandingContainer.addView(brandingLabel);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // handle a click of a user on an empty space within the item layout
                    thumbnail.handleClick();
                }
            });

        }

        void onViewRecycled() {
            itemTitleContainer.removeAllViews();
            itemImageContainer.removeAllViews();
            itemBrandingContainer.removeAllViews();
            itemView.setOnClickListener(null);
        }
    }

    class FakeItemViewHolder extends RecyclerView.ViewHolder {
        private TextView itemDescription;
        private ImageView itemImage;

        FakeItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemDescription = itemView.findViewById(R.id.fake_description);
            itemImage = itemView.findViewById(R.id.fake_background);
        }

        void bind(FakeItemModel item) {
            itemDescription.setText(item.getTitle());
            itemImage.setBackgroundColor(Color.BLUE);
        }
    }
}
