package com.joshuarichardson.fivewaystowellbeing.ui.pass_times.edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.joshuarichardson.fivewaystowellbeing.ActivityTypeImageHelper;
import com.joshuarichardson.fivewaystowellbeing.R;
import com.joshuarichardson.fivewaystowellbeing.WaysToWellbeing;
import com.joshuarichardson.fivewaystowellbeing.WellbeingHelper;
import com.joshuarichardson.fivewaystowellbeing.storage.entity.ActivityRecord;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PassTimesAdapter extends RecyclerView.Adapter<PassTimesAdapter.PassTimeViewHolder> implements Filterable {

    private final Context context;
    List<ActivityRecord> originalPasstimeItems = new ArrayList<>();
    List<ActivityRecord> passTimeItems = new ArrayList<>();
    LayoutInflater inflater;
    private PasstimeClickListener clickListener;
    private ItemCountUpdateListener itemUpdateCallback;
    private boolean isEditable;

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
                String listItemName = record.getActivityName().toLowerCase();

                if(listItemName.matches("([\\s\\w]*\\s" + filterPattern + "[\\s\\w]*)|(^" + filterPattern + "[\\s\\w]*)")) {
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

    public void editableList(boolean isEditable) {
        this.isEditable = isEditable;
        notifyDataSetChanged();
    }

    public class PassTimeViewHolder extends RecyclerView.ViewHolder {
        private MaterialButton deleteButton;
        private MaterialButton editButton;
        private TextView nameTextView;
        private TextView wayToWellbeingTextView;
        private TextView typeTextView;
        private ImageView image;
        private View editContainer;
        private TextView errorMessage;

        public PassTimeViewHolder(@NonNull View itemView) {
            super(itemView);

            this.nameTextView = itemView.findViewById(R.id.nameTextView);
            this.wayToWellbeingTextView = itemView.findViewById(R.id.wayToWellbeingTextView);
            this.typeTextView = itemView.findViewById(R.id.typeTextView);
            this.image = itemView.findViewById(R.id.list_item_image);
            this.editContainer = itemView.findViewById(R.id.update_pass_time);
            this.editButton = itemView.findViewById(R.id.edit_button);
            this.deleteButton = itemView.findViewById(R.id.delete_button);
            this.errorMessage = itemView.findViewById(R.id.error_message);
        }

        public void onBind(ActivityRecord passtime, PasstimeClickListener clickListener) {
            this.nameTextView.setText(passtime.getActivityName());
            this.typeTextView.setText(passtime.getActivityType());

            this.errorMessage.setVisibility(View.GONE);
            if(passtime.getActivityWayToWellbeing().equals("UNASSIGNED")) {
                this.wayToWellbeingTextView.setVisibility(View.GONE);
                if (passtime.getActivityTimestamp() < 1613509560000L) {
                    this.errorMessage.setVisibility(View.VISIBLE);
                }
            } else {
                this.wayToWellbeingTextView.setText(WellbeingHelper.getStringFromWayToWellbeing(WaysToWellbeing.valueOf(passtime.getActivityWayToWellbeing())));
                this.wayToWellbeingTextView.setVisibility(View.VISIBLE);
            }

            this.image.setImageResource(ActivityTypeImageHelper.getActivityImage(passtime.getActivityType()));

            // Toggle between editable and non-editable
            if(PassTimesAdapter.this.isEditable) {
                this.editContainer.setVisibility(View.VISIBLE);
            } else {
                this.editContainer.setVisibility(View.GONE);
            }

            this.editButton.setOnClickListener(v -> {
                clickListener.onEditClick(v, passtime);
            });

            this.deleteButton.setOnClickListener(v -> {
                clickListener.onDeleteClick(v, passtime);
            });

            // Reference https://medium.com/android-gate/recyclerview-item-click-listener-the-right-way-daecc838fbb9
            itemView.setOnClickListener((v) -> {
                clickListener.onItemClick(v, passtime);
            });
        }
    }

    public interface PasstimeClickListener {
        void onItemClick(View view, ActivityRecord passtime);
        void onEditClick(View view, ActivityRecord passtime);
        void onDeleteClick(View view, ActivityRecord passtime);
    }

    public interface ItemCountUpdateListener {
        void itemCount(int size);
    }
}
