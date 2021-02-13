package com.joshuarichardson.fivewaystowellbeing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PassTimesAdapter extends RecyclerView.Adapter<PassTimesAdapter.PassTimeViewHolder> {

    private final Context context;
    List<ActivityRecord> passTimeItems;
    LayoutInflater inflater;
    private PasstimeClickListener clickListener;

    public PassTimesAdapter (Context context, List<ActivityRecord> passTimeItems, PasstimeClickListener clickListener) {
        this.inflater = LayoutInflater.from(context);
        this.passTimeItems = passTimeItems;
        this.context = context;
        this.clickListener = clickListener;
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
        holder.onBind(passTimeItems.get(position), this.clickListener);
    }

    @Override
    public int getItemCount() {
        return passTimeItems.size();
    }

    public class PassTimeViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView timestampTextView;
        private TextView durationTextView;
        private TextView wayToWellbeingTextView;
        private TextView typeTextView;
        private ImageView image;

        public PassTimeViewHolder(@NonNull View itemView) {
            super(itemView);

            this.nameTextView = itemView.findViewById(R.id.nameTextView);
            this.timestampTextView = itemView.findViewById(R.id.survey_list_title);
            this.durationTextView = itemView.findViewById(R.id.durationTextView);
            this.wayToWellbeingTextView = itemView.findViewById(R.id.wayToWellbeingTextView);
            this.typeTextView = itemView.findViewById(R.id.typeTextView);
            this.image = itemView.findViewById(R.id.list_item_image);
        }

        public void onBind(ActivityRecord passtime, PasstimeClickListener clickListener) {
            this.nameTextView.setText(passtime.getActivityName());
            this.durationTextView.setText(String.format(Locale.getDefault(),"%d %s", passtime.getActivityDuration() / (1000 * 60), PassTimesAdapter.this.context.getString(R.string.minutes)));
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
            this.timestampTextView.setText(dateFormatter.format(passtime.getActivityTimestamp()));
            this.typeTextView.setText(passtime.getActivityType());

            if(passtime.getActivityWayToWellbeing().equals("UNASSIGNED")) {
                this.wayToWellbeingTextView.setVisibility(View.GONE);
            } else {
                this.wayToWellbeingTextView.setText(passtime.getActivityWayToWellbeing());
                this.wayToWellbeingTextView.setVisibility(View.VISIBLE);
            }

            this.image.setImageResource(ActivityTypeImageHelper.getActivityImage(passtime.getActivityType()));

            // Reference https://medium.com/android-gate/recyclerview-item-click-listener-the-right-way-daecc838fbb9
            itemView.setOnClickListener((v) -> {
                clickListener.onItemClick(v, passtime);
            });
        }
    }

    public interface PasstimeClickListener {
        void onItemClick(View view, ActivityRecord passtime);
    }
}
