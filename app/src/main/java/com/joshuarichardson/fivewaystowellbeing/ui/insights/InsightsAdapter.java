package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WellbeingHelper;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class InsightsAdapter extends RecyclerView.Adapter<InsightsAdapter.InsightsViewHolder> {

    private final List<InsightsItem> insightsList;
    private final Context context;
    LayoutInflater inflater;

    public InsightsAdapter(Context context, List<InsightsItem> insights) {
        this.inflater = LayoutInflater.from(context);
        this.insightsList = insights;
        this.context = context;
    }

    @NonNull
    @Override
    public InsightsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.insight_container, parent, false);
        return new InsightsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InsightsViewHolder holder, int position) {
        holder.onBind(this.insightsList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.insightsList.size();
    }

    public class InsightsViewHolder extends RecyclerView.ViewHolder {

        View small_card;
        View large_card;

        TextView titleType;
        TextView info;

        public InsightsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.small_card = itemView.findViewById(R.id.small_insight);
            this.large_card = itemView.findViewById(R.id.large_insight);

        }

        public void onBind(InsightsItem insightsItem) {
            View layout;
            if(insightsItem.getColumnWidth() == 2) {
                this.titleType = this.large_card.findViewById(R.id.insight_title_type);
                this.info = this.large_card.findViewById(R.id.insight_description);
                layout = this.large_card.findViewById(R.id.insight_card_layout);
                this.large_card.setVisibility(View.VISIBLE);
                this.small_card.setVisibility(View.GONE);
                this.info.setText(insightsItem.getInfo());
            } else {
                this.titleType = this.small_card.findViewById(R.id.insight_title_type);
                this.info = this.small_card.findViewById(R.id.insight_description);
                layout = this.small_card.findViewById(R.id.insight_card_layout);
                this.small_card.setVisibility(View.VISIBLE);
                this.large_card.setVisibility(View.GONE);
                this.info.setText(String.format(Locale.getDefault(), "%d", insightsItem.getCurrentValue()));
                int difference = insightsItem.getValueDifference();

                TextView trendIndicator = this.small_card.findViewById(R.id.trend_previous_period);
                ImageView trendIndicatorImage = this.small_card.findViewById(R.id.trend_indicator);
                if (difference > 0) {
                    trendIndicator.setText(String.format(Locale.getDefault(), "+%d", difference));
                    trendIndicatorImage.setImageResource(R.drawable.icon_trend_up);
                    trendIndicator.setVisibility(View.VISIBLE);
                    trendIndicatorImage.setVisibility(View.VISIBLE);
                } else if (difference < 0) {
                    trendIndicator.setText(String.format(Locale.getDefault(), "%d", difference));
                    trendIndicatorImage.setImageResource(R.drawable.icon_trend_down);
                    trendIndicator.setVisibility(View.VISIBLE);
                    trendIndicatorImage.setVisibility(View.VISIBLE);
                } else {
                    trendIndicator.setVisibility(View.GONE);
                    trendIndicatorImage.setVisibility(View.GONE);
                }

                // Show correct colour for the item
                ImageView typeImage = this.small_card.findViewById(R.id.wellbeing_type_image);
                GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(InsightsAdapter.this.context, R.drawable.wellbeing_type_indicator);
                if (drawable != null) {
                    drawable = (GradientDrawable) drawable.mutate();
                    drawable.setColor(WellbeingHelper.getColor(InsightsAdapter.this.context, insightsItem.getWayToWellbeing().toString()));
                    typeImage.setImageDrawable(drawable);
                }
            }

            View divider = layout.findViewById(R.id.insight_divider);

            if(insightsItem.getSpecialView() != null) {
                divider.setVisibility(View.VISIBLE);
                ((LinearLayout)layout).addView(insightsItem.getSpecialView());
            } else {
                divider.setVisibility(View.GONE);
            }

            this.titleType.setText(insightsItem.getTitle());
        }
    }
}
