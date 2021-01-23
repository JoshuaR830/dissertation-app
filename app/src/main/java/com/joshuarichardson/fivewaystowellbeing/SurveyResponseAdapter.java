package com.joshuarichardson.fivewaystowellbeing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SurveyResponseAdapter extends RecyclerView.Adapter<SurveyResponseAdapter.SurveyResponseViewHolder> {

    private final LayoutInflater inflater;
    private final List<SurveyResponse> surveyResponses;

    public SurveyResponseAdapter(Context context, List<SurveyResponse> list) {
        this.inflater = LayoutInflater.from(context);
        this.surveyResponses = list;
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

        private TextView surveyTitle;
        private TextView surveyDescription;

        public SurveyResponseViewHolder(@NonNull View itemView) {
            super(itemView);
            this.surveyTitle =  itemView.findViewById(R.id.survey_list_title);
            this.surveyDescription = itemView.findViewById(R.id.survey_list_description);
        }

        public void onBind(SurveyResponse response) {
            this.surveyTitle.setText(response.getTitle());
            this.surveyDescription.setText(response.getDescription());
        }
    }
}
