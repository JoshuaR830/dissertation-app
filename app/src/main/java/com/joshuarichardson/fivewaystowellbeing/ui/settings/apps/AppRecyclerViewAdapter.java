package com.joshuarichardson.fivewaystowellbeing.ui.settings.apps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class AppRecyclerViewAdapter extends RecyclerView.Adapter<AppRecyclerViewAdapter.SurveyResponseViewHolder> {

    private final LayoutInflater inflater;
    private final List<AppItem> appListData;
    private final Context context;
    private final OnDropdownClick dropdownCallback;
    private ArrayList<Long> activityIds;
    private ArrayList<String> activityNames;
    private ArrayList<String> activityWaysToWellbeing;

    public AppRecyclerViewAdapter(Context context, List<AppItem> list, OnDropdownClick dropdownCallback) {
        this.inflater = LayoutInflater.from(context);
        this.appListData = list;
        this.context = context;
        this.dropdownCallback = dropdownCallback;
    }

    @NonNull
    @Override
    // Needs to make the holder without populating it
    public SurveyResponseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = this.inflater.inflate(R.layout.app_list_item, parent, false);
        return new SurveyResponseViewHolder(itemView);
    }

    @Override
    // Needs to add the content to the ViewHolder
    public void onBindViewHolder(@NonNull SurveyResponseViewHolder holder, int position) {
        holder.onBind(this.appListData.get(position));
    }

    @Override
    public int getItemCount() {
        return this.appListData.size();
    }

    public void setValues(List<AppItem> appData) {
        this.appListData.clear();
        this.appListData.addAll(appData);
        notifyDataSetChanged();
    }

    public void setListValues(ArrayList<Long> activityIds, ArrayList<String> activityNames, ArrayList<String> activityWaysToWellbeing) {
        this.activityIds = activityIds;
        this.activityNames = activityNames;
        this.activityWaysToWellbeing = activityWaysToWellbeing;

    }

    public class SurveyResponseViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {

        private String packageName;

        private TextView appName;
        private TextView activityId;
        private TextView activityWayToWellbeing;
        private Spinner dropDown;
        private ImageView image;
        private AppItem response;

        public SurveyResponseViewHolder(@NonNull View itemView) {
            super(itemView);
            this.appName =  itemView.findViewById(R.id.app_name_text_view);
            this.activityId = itemView.findViewById(R.id.associated_activity_text_view);
            this.activityWayToWellbeing = itemView.findViewById(R.id.way_to_wellbeing_text_view);
            this.dropDown = itemView.findViewById(R.id.drop_down_input);
            this.image = itemView.findViewById(R.id.list_item_image);
        }

        public void onBind(AppItem response) {
            this.response = response;
            this.packageName = response.getPackageName();
            this.appName.setText(response.getAppName());
            this.activityId.setText(response.getActivityName());
            this.activityWayToWellbeing.setText(response.getWayToWellbeing());

            int index = AppRecyclerViewAdapter.this.activityIds.indexOf(response.getActivityId());
            if(index < 0) {
                index = 0;
            }

            SpinnerAdapter adapter = new ArrayAdapter<>(AppRecyclerViewAdapter.this.context, R.layout.item_list_text, activityNames);
            this.dropDown.setAdapter(adapter);

            this.dropDown.setSelection(index);

            this.dropDown.setOnItemSelectedListener(this);

            PackageManager packageManager = AppRecyclerViewAdapter.this.context.getPackageManager();
            Drawable appIcon;

            try {
                appIcon = packageManager.getApplicationIcon(response.getPackageName());
            } catch (PackageManager.NameNotFoundException e) {
                appIcon = ContextCompat.getDrawable(AppRecyclerViewAdapter.this.context, R.drawable.activity_type_app);
            }

            this.image.setImageDrawable(appIcon);
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(id == 0) {
                this.response.setActivityName("");
                this.response.setActivityId(0);
                this.activityId.setText(context.getString(R.string.no_activity_assigned));
                this.activityWayToWellbeing.setText("");
                AppRecyclerViewAdapter.this.dropdownCallback.onItemSelected(0, this.packageName);
                return;
            }

            String listItem = AppRecyclerViewAdapter.this.activityNames.get((int) id);
            long listNum = AppRecyclerViewAdapter.this.activityIds.get((int) id);

            this.response.setActivityName(listItem);
            this.response.setActivityId(listNum);

            this.activityId.setText(listItem);
            this.activityWayToWellbeing.setText(AppRecyclerViewAdapter.this.activityWaysToWellbeing.get((int) id));

            AppRecyclerViewAdapter.this.dropdownCallback.onItemSelected(listNum, this.packageName);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            this.dropDown.setSelection(0);
        }
    }

    public interface OnDropdownClick {
        void onItemSelected(long activityId, String packageName);
    }
}
