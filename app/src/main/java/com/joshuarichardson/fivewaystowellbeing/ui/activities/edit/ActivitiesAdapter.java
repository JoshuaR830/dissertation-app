package com.joshuarichardson.fivewaystowellbeing.ui.activities.edit;

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

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.ActivityViewHolder> implements Filterable {

    private final Context context;
    List<ActivityRecord> originalActivityItems = new ArrayList<>();
    List<ActivityRecord> activityItems = new ArrayList<>();
    LayoutInflater inflater;
    private ActivityClickListener clickListener;
    private ItemCountUpdateListener itemUpdateCallback;
    private boolean isEditable;

    public ActivitiesAdapter(Context context, List<ActivityRecord> activityItems, ActivityClickListener clickListener, ItemCountUpdateListener itemUpdateCallback) {
        this.inflater = LayoutInflater.from(context);
        this.originalActivityItems.addAll(activityItems);
        this.activityItems.addAll(activityItems);
        this.context = context;
        this.clickListener = clickListener;
        this.itemUpdateCallback = itemUpdateCallback;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // This creates the view holder but does not populate it
        View view = this.inflater.inflate(R.layout.pass_time_record_list_item, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        // This populates the view holder
        holder.onBind(this.activityItems.get(position), this.clickListener);
    }

    @Override
    public int getItemCount() {
        return this.activityItems.size();
    }

    @Override
    public Filter getFilter() {
        return this.searchActivityFilter;
    }

    Filter searchActivityFilter = new Filter() {

        @Override
        // Reference https://www.tutorialspoint.com/how-to-filter-a-recyclerview-with-a-searchview-on-android
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ActivityRecord> filteredActivityRecords = new ArrayList<>();
            FilterResults results = new FilterResults();
            if(constraint == null || constraint.length() == 0) {
                // Set to each item of the original list - without maintaining a reference to it
                results.values = ActivitiesAdapter.this.originalActivityItems;
                return results;
            }

            // If there is a search pattern this will find all of the items
            String filterPattern = constraint.toString().toLowerCase().trim();
            filterPattern = filterPattern.replaceAll("[^A-Za-z0-9 ]","");
            for(ActivityRecord record : ActivitiesAdapter.this.originalActivityItems) {
                String listItemName = record.getActivityName().toLowerCase();
                listItemName = listItemName.replaceAll("[^A-Za-z0-9 ]","");
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
            ActivitiesAdapter.this.activityItems.clear();
            ActivitiesAdapter.this.activityItems.addAll((List)results.values);

            // Update the recycler view
            notifyDataSetChanged();
            ActivitiesAdapter.this.itemUpdateCallback.itemCount(ActivitiesAdapter.this.getItemCount());
        }
    };

    public void setValues(List<ActivityRecord> activityData, String searchTerm) {
        this.originalActivityItems.clear();
        this.originalActivityItems.addAll(activityData);
        getFilter().filter(searchTerm);
    }

    public void editableList(boolean isEditable) {
        this.isEditable = isEditable;
        notifyDataSetChanged();
    }

    public class ActivityViewHolder extends RecyclerView.ViewHolder {
        private MaterialButton deleteButton;
        private MaterialButton editButton;
        private TextView nameTextView;
        private TextView wayToWellbeingTextView;
        private TextView typeTextView;
        private ImageView image;
        private View editContainer;
        private TextView errorMessage;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);

            this.nameTextView = itemView.findViewById(R.id.app_name_text_view);
            this.wayToWellbeingTextView = itemView.findViewById(R.id.way_to_wellbeing_text_view);
            this.typeTextView = itemView.findViewById(R.id.associated_activity_text_view);
            this.image = itemView.findViewById(R.id.list_item_image);
            this.editContainer = itemView.findViewById(R.id.update_pass_time);
            this.editButton = itemView.findViewById(R.id.edit_button);
            this.deleteButton = itemView.findViewById(R.id.delete_button);
            this.errorMessage = itemView.findViewById(R.id.error_message);
        }

        public void onBind(ActivityRecord activity, ActivityClickListener clickListener) {
            this.nameTextView.setText(activity.getActivityName());
            this.typeTextView.setText(activity.getActivityType());

            this.errorMessage.setVisibility(View.GONE);
            if(activity.getActivityWayToWellbeing().equals("UNASSIGNED")) {
                this.wayToWellbeingTextView.setVisibility(View.GONE);
                if (activity.getActivityTimestamp() < 1613509560000L) {
                    this.errorMessage.setVisibility(View.VISIBLE);
                }
            } else {
                this.wayToWellbeingTextView.setText(WellbeingHelper.getStringFromWayToWellbeing(WaysToWellbeing.valueOf(activity.getActivityWayToWellbeing())));
                this.wayToWellbeingTextView.setVisibility(View.VISIBLE);
            }

            this.image.setImageResource(ActivityTypeImageHelper.getActivityImage(activity.getActivityType()));

            // Toggle between editable and non-editable
            if(ActivitiesAdapter.this.isEditable) {
                this.editContainer.setVisibility(View.VISIBLE);
            } else {
                this.editContainer.setVisibility(View.GONE);
            }

            this.editButton.setOnClickListener(v -> {
                clickListener.onEditClick(v, activity);
            });

            this.deleteButton.setOnClickListener(v -> {
                clickListener.onDeleteClick(v, activity);
            });

            // Reference https://medium.com/android-gate/recyclerview-item-click-listener-the-right-way-daecc838fbb9
            itemView.setOnClickListener((v) -> {
                clickListener.onItemClick(v, activity);
            });
        }
    }

    public interface ActivityClickListener {
        void onItemClick(View view, ActivityRecord activity);
        void onEditClick(View view, ActivityRecord activity);
        void onDeleteClick(View view, ActivityRecord activity);
    }

    public interface ItemCountUpdateListener {
        void itemCount(int size);
    }
}
