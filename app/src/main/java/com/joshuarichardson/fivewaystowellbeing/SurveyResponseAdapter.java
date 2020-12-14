package com.joshuarichardson.fivewaystowellbeing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponse;

import java.util.List;
import java.util.Locale;

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
        // Pass the values to set
        int timestamp = 5884567;
        String wayToWellbeing = "Connect";

        holder.onBind(timestamp, wayToWellbeing);
    }

    @Override
    public int getItemCount() {
        return this.surveyResponses.size();
    }

    public class SurveyResponseViewHolder extends RecyclerView.ViewHolder {

        TextView timestampText;
        TextView wayToWellbeingText;

        public SurveyResponseViewHolder(@NonNull View itemView) {
            super(itemView);
            this.timestampText =  itemView.findViewById(R.id.timestampTextView);
            this.wayToWellbeingText = itemView.findViewById(R.id.wayToWellbeingTextView);
        }

        public void onBind(int timestamp, String wayToWellbeing) {
            this.timestampText.setText(String.format(Locale.getDefault(),"%d", timestamp));
            this.wayToWellbeingText.setText(wayToWellbeing);
        }
    }
}
