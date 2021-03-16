package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.TimeHelper;
import com.joshuarichardson.fivewaystowellbeing.WellbeingHelper;
import com.joshuarichardson.fivewaystowellbeing.ui.graphs.LineGraphHelper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class InsightsAdapter extends RecyclerView.Adapter<InsightsAdapter.InsightsViewHolder> {

    private final List<InsightsItem> insightsList;
    private final Context context;
    private final DateClickListener clickListener;
    LayoutInflater inflater;
    private FragmentManager fragmentManager;

    public InsightsAdapter(Context context, List<InsightsItem> insights, DateClickListener clickListener, FragmentManager fragmentManager) {
        this.inflater = LayoutInflater.from(context);
        this.insightsList = insights;
        this.context = context;
        this.clickListener = clickListener;
        this.fragmentManager = fragmentManager;
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
        View graph_card;
        View date_picker_card;

        TextView titleType;
        TextView info;

        public InsightsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.small_card = itemView.findViewById(R.id.small_insight);
            this.large_card = itemView.findViewById(R.id.large_insight);
            this.graph_card = itemView.findViewById(R.id.graph_insight);
            this.date_picker_card = itemView.findViewById(R.id.date_picker_insight);

        }

        public void onBind(InsightsItem insightsItem) {

            this.small_card.setVisibility(View.GONE);
            this.large_card.setVisibility(View.GONE);
            this.graph_card.setVisibility(View.GONE);
            this.date_picker_card.setVisibility(View.GONE);

            if(insightsItem.getType() == InsightType.DATE_PICKER_CARD) {
                this.titleType = this.date_picker_card.findViewById(R.id.time_chip);
                Chip chip = this.date_picker_card.findViewById(R.id.time_chip);

                long startTime = 1614556800000L;
                long endTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    // Reference https://material.io/components/date-pickers/android#timezones
                    LocalDateTime localStart = LocalDateTime.now();
                    startTime = localStart.atZone(ZoneId.ofOffset("UTC", ZoneOffset.UTC)).toInstant().toEpochMilli();
                    LocalDateTime localEnd = LocalDateTime.of(2021, 3, 1, 0, 0);
                    endTime = localEnd.atZone(ZoneId.ofOffset("UTC", ZoneOffset.UTC)).toInstant().toEpochMilli();
                }

                final long finalStartTime = startTime;
                final long finalEndTime = endTime;

                chip.setOnClickListener(v -> {
                    CalendarConstraints constraints = new CalendarConstraints.Builder()
                        .setStart(finalStartTime)
                        .setEnd(finalEndTime)
                        .build();

                    MaterialDatePicker<Pair<Long, Long>> calendar = MaterialDatePicker.Builder
                        .dateRangePicker()
                        .setCalendarConstraints(constraints)
                        .build();

                    calendar.show(InsightsAdapter.this.fragmentManager, "start");

                    // Send the calendar values through
                    calendar.addOnPositiveButtonClickListener(selection -> {
                        long second = TimeHelper.getEndOfDay(selection.second);
                        InsightsAdapter.this.clickListener.updateInsights(InsightsViewHolder.this.itemView, selection.first, second);
                    });
                });
                this.date_picker_card.setVisibility(View.VISIBLE);

            } else if(insightsItem.getType() == InsightType.DOUBLE_GRAPH) {
                // Reference https://weeklycoding.com/mpandroidchart-documentation/getting-started/
                this.titleType = this.graph_card.findViewById(R.id.insight_title_type);
                LineGraphHelper.drawGraph(InsightsAdapter.this.context, this.graph_card, insightsItem);
            } else if(insightsItem.getType() == InsightType.DOUBLE_INFO_CARD) {
                this.titleType = this.large_card.findViewById(R.id.insight_title_type);
                this.info = this.large_card.findViewById(R.id.insight_description);
                this.large_card.setVisibility(View.VISIBLE);
                this.info.setText(insightsItem.getInfo());
            } else {
                this.titleType = this.small_card.findViewById(R.id.insight_title_type);
                this.info = this.small_card.findViewById(R.id.insight_description);
                this.small_card.setVisibility(View.VISIBLE);
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

            this.titleType.setText(insightsItem.getTitle());
        }
    }

    public interface DateClickListener {
        void updateInsights(View itemView, long first, long second);
    }
}
