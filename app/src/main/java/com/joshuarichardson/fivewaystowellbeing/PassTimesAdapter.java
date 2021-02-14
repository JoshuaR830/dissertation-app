package com.joshuarichardson.fivewaystowellbeing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PassTimesAdapter extends RecyclerView.Adapter<PassTimesAdapter.PassTimeViewHolder> implements Filterable {

    private final Context context;
    List<ActivityRecord> originalPasstimeItems = new ArrayList<>();
    List<ActivityRecord> passTimeItems = new ArrayList<>();
    LayoutInflater inflater;
    private PasstimeClickListener clickListener;
    private ItemCountUpdateListener itemUpdateCallback;

    public PassTimesAdapter (Context context, List<ActivityRecord> passTimeItems, PasstimeClickListener clickListener, ItemCountUpdateListener itemUpdateCallback) {
        this.inflater = LayoutInflater.from(context);
        this.originalPasstimeItems.addAll(passTimeItems);
        this.passTimeItems.addAll(passTimeItems);
        this.context = context;
        this.clickListener = clickListener;
        this.itemUpdateCallback = itemUpdateCallback;
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
        holder.onBind(this.passTimeItems.get(position), this.clickListener);
    }

    @Override
    public int getItemCount() {
        return this.passTimeItems.size();
    }

    @Override
    public Filter getFilter() {
        return this.searchPasstimeFilter;
    }

    Filter searchPasstimeFilter = new Filter() {

        @Override
        // Reference https://www.tutorialspoint.com/how-to-filter-a-recyclerview-with-a-searchview-on-android
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ActivityRecord> filteredActivityRecords = new ArrayList<>();
            FilterResults results = new FilterResults();
            if(constraint == null || constraint.length() == 0) {
                // Set to each item of the original list - without maintaining a reference to it
                results.values = PassTimesAdapter.this.originalPasstimeItems;
                return results;
            }

            // If there is a search pattern this will find all of the items
            String filterPattern = constraint.toString().toLowerCase().trim();
            for(ActivityRecord record : PassTimesAdapter.this.originalPasstimeItems) {
                if(record.getActivityName().toLowerCase().startsWith(filterPattern)) {
                    // Adds items that match the filter
                    filteredActivityRecords.add(record);
                }
            }

            results.values = filteredActivityRecords;
            return results;
        }

        @Override
        // Reference: https://www.tutorialspoint.com/how-to-filter-a-recyclerview-with-a-searchview-on-android
        protected void publishResults(CharSequence constraint, FilterResults results) {
            PassTimesAdapter.this.passTimeItems.clear();
            PassTimesAdapter.this.passTimeItems.addAll((List)results.values);

            // Update the recycler view
            notifyDataSetChanged();
            PassTimesAdapter.this.itemUpdateCallback.itemCount(PassTimesAdapter.this.getItemCount());
        }
    };

    public void setValues(List<ActivityRecord> passTimeData, String searchTerm) {
        this.originalPasstimeItems.clear();
        this.originalPasstimeItems.addAll(passTimeData);
        getFilter().filter(searchTerm);
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

    public interface ItemCountUpdateListener {
        void itemCount(int size);
    }
}
