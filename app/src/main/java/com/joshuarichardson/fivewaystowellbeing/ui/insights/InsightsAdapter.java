package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import android.content.Context;
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
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.ui.graphs.LineGraphHelper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class InsightsAdapter extends RecyclerView.Adapter<InsightsAdapter.InsightsViewHolder> {

    private final List<InsightsItem> insightsList;
    private final Context context;
    private final DateClickListener clickListener;
    private final ChipInfoCallback chipInfoCallback;
    LayoutInflater inflater;
    private FragmentManager fragmentManager;

    public InsightsAdapter(Context context, List<InsightsItem> insights, DateClickListener clickListener, FragmentManager fragmentManager, ChipInfoCallback chipInfoCallback) {
        this.inflater = LayoutInflater.from(context);
        this.insightsList = insights;
        this.context = context;
        this.clickListener = clickListener;
        this.fragmentManager = fragmentManager;
        this.chipInfoCallback = chipInfoCallback;
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

        View smallCard;
        View largeCard;
        View graphCard;
        View datePickerCard;
        private View suggestionsCard;

        TextView titleType;
        TextView info;

        public InsightsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.smallCard = itemView.findViewById(R.id.small_insight);
            this.largeCard = itemView.findViewById(R.id.large_insight);
            this.graphCard = itemView.findViewById(R.id.graph_insight);
            this.datePickerCard = itemView.findViewById(R.id.date_picker_insight);
            this.suggestionsCard = itemView.findViewById(R.id.suggestion_insight);

        }

        public void onBind(InsightsItem insightsItem) {

            this.smallCard.setVisibility(View.GONE);
            this.largeCard.setVisibility(View.GONE);
            this.graphCard.setVisibility(View.GONE);
            this.datePickerCard.setVisibility(View.GONE);
            this.suggestionsCard.setVisibility(View.GONE);

            if(insightsItem.getType() == InsightType.SUGGESTION_CARD) {
                if(!insightsItem.isShouldShow()) {
                    return;
                }

                this.titleType = this.suggestionsCard.findViewById(R.id.way_to_wellbeing);
                TextView activityHelp = this.suggestionsCard.findViewById(R.id.insight_title_type);
                TextView description = this.suggestionsCard.findViewById(R.id.insight_description);

                activityHelp.setText(insightsItem.getActivityDescription());
                description.setText(insightsItem.getInfo());

                // Show correct colour for the item
                ImageView typeImage = this.suggestionsCard.findViewById(R.id.wellbeing_type_image);
                WayToWellbeingImageColorizer.colorize(InsightsAdapter.this.context, typeImage, insightsItem.getWayToWellbeing());

                this.suggestionsCard.setVisibility(View.VISIBLE);

            } else if(insightsItem.getType() == InsightType.DATE_PICKER_CARD) {
                this.titleType = this.datePickerCard.findViewById(R.id.time_chip);
                Chip chip = this.datePickerCard.findViewById(R.id.time_chip);

                long startTime = 1614556800000L;
                long endTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    // Reference https://material.io/components/date-pickers/android#timezones
                    LocalDateTime localStart = LocalDateTime.of(2021, 3, 1, 0, 0);
                    startTime = localStart.atZone(ZoneId.ofOffset("UTC", ZoneOffset.UTC)).toInstant().toEpochMilli();
                    LocalDateTime localEnd = LocalDateTime.now();
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
                this.datePickerCard.setVisibility(View.VISIBLE);

            } else if(insightsItem.getType() == InsightType.DOUBLE_GRAPH) {
                // Reference https://weeklycoding.com/mpandroidchart-documentation/getting-started/
                this.titleType = this.graphCard.findViewById(R.id.insight_title_type);
                LineGraphHelper.drawGraph(InsightsAdapter.this.context, this.graphCard, insightsItem, chipInfoCallback);
            } else if(insightsItem.getType() == InsightType.DOUBLE_INFO_CARD) {
                this.titleType = this.largeCard.findViewById(R.id.insight_title_type);
                this.info = this.largeCard.findViewById(R.id.insight_description);
                this.largeCard.setVisibility(View.VISIBLE);
                this.info.setText(insightsItem.getInfo());
            } else {
                this.titleType = this.smallCard.findViewById(R.id.insight_title_type);
                this.info = this.smallCard.findViewById(R.id.insight_description);
                this.smallCard.setVisibility(View.VISIBLE);
                this.info.setText(String.format(Locale.getDefault(), "%d", insightsItem.getCurrentValue()));
                int difference = insightsItem.getValueDifference();

                TextView trendIndicator = this.smallCard.findViewById(R.id.trend_previous_period);
                ImageView trendIndicatorImage = this.smallCard.findViewById(R.id.trend_indicator);
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
                ImageView typeImage = this.smallCard.findViewById(R.id.wellbeing_type_image);
                WayToWellbeingImageColorizer.colorize(InsightsAdapter.this.context, typeImage, insightsItem.getWayToWellbeing());
            }

            this.titleType.setText(insightsItem.getTitle());
        }
    }

    public interface DateClickListener {
        void updateInsights(View itemView, long first, long second);
    }

    public interface ChipInfoCallback {
        void displaySuggestionChip(View graphCard, long startTime, long endTime, WaysToWellbeing wayToWellbeing);
    }
}
