package com.joshuarichardson.fivewaystowellbeing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.storage.WellbeingGraphValueHelper;
import com.joshuarichardson.fivewaystowellbeing.ui.graphs.WellbeingGraphView;
import com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys.IndividualSurveyActivity;
import com.joshuarichardson.fivewaystowellbeing.ui.view.HistoryPageData;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing.UNASSIGNED;

public class SurveyResponseAdapter extends RecyclerView.Adapter<SurveyResponseAdapter.SurveyResponseViewHolder> {

    private final LayoutInflater inflater;
    private final List<HistoryPageData> historyPageData;
    private final Context context;

    public SurveyResponseAdapter(Context context, List<HistoryPageData> list) {
        this.inflater = LayoutInflater.from(context);
        this.historyPageData = list;
        this.context = context;
    }

    @NonNull
    @Override
    // Needs to make the holder without populating it
    public SurveyResponseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = this.inflater.inflate(R.layout.survey_response_list_item, parent, false);
        return new SurveyResponseViewHolder(itemView);
    }

    @Override
    // Needs to add the content to the ViewHolder
    public void onBindViewHolder(@NonNull SurveyResponseViewHolder holder, int position) {
        holder.onBind(this.historyPageData.get(position));
    }

    @Override
    public int getItemCount() {
        return this.historyPageData.size();
    }

    public class SurveyResponseViewHolder extends RecyclerView.ViewHolder {

        private final WellbeingGraphView graphView;
        private TextView surveyTitle;
        private TextView surveyDescription;
        private FrameLayout surveyItemFrame;
        private Button viewMoreButton;
        private ImageView image;

        public SurveyResponseViewHolder(@NonNull View itemView) {
            super(itemView);
            this.surveyTitle =  itemView.findViewById(R.id.survey_list_title);
            this.surveyDescription = itemView.findViewById(R.id.survey_list_description);
            this.surveyItemFrame = itemView.findViewById(R.id.surveys_completed_frame_layout);
            this.viewMoreButton = itemView.findViewById(R.id.view_more_button);

            this.graphView = new WellbeingGraphView(SurveyResponseAdapter.this.context, DisplayHelper.dpToPx(SurveyResponseAdapter.this.context, 72), new WellbeingGraphValueHelper(0, 0, 0, 0, 0));
            this.surveyItemFrame.addView(this.graphView);
        }

        public void onBind(HistoryPageData response) {
            this.surveyTitle.setText(TimeFormatter.formatTimeAsDayMonthYearString(response.getSurveyResponseTimestamp()));
            this.surveyDescription.setText(response.getDescription());

            if(response.getSurveyResponseWayToWellbeing() != null && WaysToWellbeing.valueOf(response.getSurveyResponseWayToWellbeing()) != UNASSIGNED) {
                this.image = new ImageView(SurveyResponseAdapter.this.context);
                image.setImageResource(WellbeingHelper.getImage(WaysToWellbeing.valueOf(response.getSurveyResponseWayToWellbeing())));
                this.image.setLayoutParams(new FrameLayout.LayoutParams(DisplayHelper.dpToPx(SurveyResponseAdapter.this.context, 48), DisplayHelper.dpToPx(SurveyResponseAdapter.this.context, 72), Gravity.CENTER));
                this.graphView.setVisibility(View.GONE);
                this.surveyItemFrame.addView(image);
                this.image.setVisibility(View.VISIBLE);
            } else {
                this.graphView.setVisibility(View.VISIBLE);
                this.graphView.updateValues(response.getWellbeingValues());

                if(this.image != null) {
                    this.image.setVisibility(View.GONE);
                }
            }

            this.viewMoreButton.setOnClickListener(v -> {

                // ToDo - this would probably be better as a callback
                Intent surveyIntent = new Intent(SurveyResponseAdapter.this.context, IndividualSurveyActivity.class);
                Bundle surveyBundle = new Bundle();
                surveyBundle.putLong("survey_id", response.getSurveyResponseId());
                surveyBundle.putLong("start_time", response.getSurveyResponseTimestamp());
                surveyIntent.putExtras(surveyBundle);
                SurveyResponseAdapter.this.context.startActivity(surveyIntent);
            });

            // Catch the exception if the user does not set a value
            WaysToWellbeing way;
            try {
                way = WaysToWellbeing.valueOf(response.getSurveyResponseWayToWellbeing());
            } catch(IllegalArgumentException e) {
                way = UNASSIGNED;
            }
        }
    }
}
