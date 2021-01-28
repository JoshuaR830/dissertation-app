package com.joshuarichardson.fivewaystowellbeing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PassTimesAdapter extends RecyclerView.Adapter<PassTimesAdapter.PassTimeViewHolder> {

    List<ActivityRecord> passTimeItems;
    LayoutInflater inflater;

    public PassTimesAdapter (Context context, List<ActivityRecord> passTimeItems) {
        this.inflater = LayoutInflater.from(context);
        this.passTimeItems = passTimeItems;
    }

    @NonNull
    @Override
    public PassTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This creates the view holder but does not populate it
        View view = this.inflater.inflate(R.layout.pass_time_record_list_item, parent, false);
        return new PassTimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PassTimeViewHolder holder, int position) {
        // This populates the view holder
        ActivityRecord passTime = passTimeItems.get(position);
        holder.onBind(passTime.getActivityName(), passTime.getActivityDuration(), passTime.getActivityTimestamp(), passTime.getActivityType());
    }

    @Override
    public int getItemCount() {
        return passTimeItems.size();
    }

    public class PassTimeViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView timestampTextView;
        private TextView durationTextView;
        private TextView typeTextView;
        private ImageView image;

        public PassTimeViewHolder(@NonNull View itemView) {
            super(itemView);
;
            this.nameTextView = itemView.findViewById(R.id.nameTextView);
            this.timestampTextView = itemView.findViewById(R.id.survey_list_title);
            this.durationTextView = itemView.findViewById(R.id.durationTextView);
            this.typeTextView = itemView.findViewById(R.id.typeTextView);
            this.image = itemView.findViewById(R.id.list_item_image);
        }

        public void onBind(String name, long duration, long timestamp, String type) {
            nameTextView.setText(name);
            durationTextView.setText(String.format(Locale.getDefault(), "%d", duration));
            timestampTextView.setText(String.format(Locale.getDefault(), "%d", timestamp));
            typeTextView.setText(type);

            image.setImageResource(ActivityTypeImageHelper.getActivityImage(type));
        }
    }
}
