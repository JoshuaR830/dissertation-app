package com.joshuarichardson.fivewaystowellbeing.ui.individual_surveys;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.SurveyResponseElement;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IndividualSurveyAdapter extends RecyclerView.Adapter<IndividualSurveyAdapter.IndividualSurveyViewHolder> {

    LayoutInflater inflater;
    List<SurveyResponseElement> surveyElements;

    public IndividualSurveyAdapter(Context context, List<SurveyResponseElement> surveyElements) {
        this.inflater = LayoutInflater.from(context);
        this.surveyElements = surveyElements;
    }

    @NonNull
    @Override
    public IndividualSurveyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = this.inflater.inflate(R.layout.activity_item, parent, false);
        return new IndividualSurveyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IndividualSurveyViewHolder holder, int position) {
        holder.onBind(this.surveyElements.get(position));
    }

    @Override
    public int getItemCount() {
        return this.surveyElements.size();
    }

    public class IndividualSurveyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView note;

        public IndividualSurveyViewHolder(@NonNull View itemView) {
            super(itemView);

            this.title = itemView.findViewById(R.id.activity_text);
            this.note = itemView.findViewById(R.id.activity_note_text);
        }

        public void onBind(SurveyResponseElement element) {
            this.title.setText(element.getQuestion());
            this.note.setText(element.getAnswer());
        }
    }
}
