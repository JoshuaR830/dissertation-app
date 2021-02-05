package com.joshuarichardson.fivewaystowellbeing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;
import com.joshuarichardson.fivewaystowellbeing.ui.graphs.WellbeingGraphView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SurveyResponseAdapter extends RecyclerView.Adapter<SurveyResponseAdapter.SurveyResponseViewHolder> {

    private final LayoutInflater inflater;
    private final List<SurveyResponse> surveyResponses;
    private final Context context;

    public SurveyResponseAdapter(Context context, List<SurveyResponse> list) {
        this.inflater = LayoutInflater.from(context);
        this.surveyResponses = list;
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
        holder.onBind(this.surveyResponses.get(position));
    }

    @Override
    public int getItemCount() {
        return this.surveyResponses.size();
    }

    public class SurveyResponseViewHolder extends RecyclerView.ViewHolder {

        private final WellbeingGraphView graphView;
        private TextView surveyTitle;
        private TextView surveyDescription;
        private ImageView surveyImage;

        public SurveyResponseViewHolder(@NonNull View itemView) {
            super(itemView);
            this.surveyTitle =  itemView.findViewById(R.id.survey_list_title);
            this.surveyDescription = itemView.findViewById(R.id.survey_list_description);
            this.surveyImage = itemView.findViewById(R.id.surveys_completed_image);

            FrameLayout canvasContainer = itemView.findViewById(R.id.surveys_completed_frame_layout);

            int[] values = new int[]{50, 30, 20, 10, 90};
            this.graphView = new WellbeingGraphView(SurveyResponseAdapter.this.context, DisplayHelper.dpToPx(SurveyResponseAdapter.this.context, 72), values);
            canvasContainer.addView(this.graphView);
        }

        public void onBind(SurveyResponse response) {

            // ToDo - don't generate this randomly - use a value from a database
            Random random = new Random();
            this.graphView.updateValues(new int[]{random.nextInt(100), random.nextInt(100), random.nextInt(100), random.nextInt(100), random.nextInt(100)});

            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

//            this.surveyTitle.setText(response.getTitle());
            this.surveyTitle.setText(dateFormatter.format(response.getSurveyResponseTimestamp()));
            this.surveyDescription.setText(response.getDescription());

            // Catch the exception if the user does not set a value
            WaysToWellbeing way;
            try {
                way = WaysToWellbeing.valueOf(response.getSurveyResponseWayToWellbeing());
            } catch(IllegalArgumentException e) {
                way = WaysToWellbeing.UNASSIGNED;
            }

//            this.surveyImage.setImageResource(WellbeingHelper.getImage(way));
        }
    }
}
