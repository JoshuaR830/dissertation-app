package com.joshuarichardson.fivewaystowellbeing.ui.history.apps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class AppRecyclerViewAdapter extends RecyclerView.Adapter<AppRecyclerViewAdapter.SurveyResponseViewHolder> {

    private final LayoutInflater inflater;
    private final List<AppItem> appListData;
    private final Context context;

    public AppRecyclerViewAdapter(Context context, List<AppItem> list) {
        this.inflater = LayoutInflater.from(context);
        this.appListData = list;
        this.context = context;
    }

    @NonNull
    @Override
    // Needs to make the holder without populating it
    public SurveyResponseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = this.inflater.inflate(R.layout.pass_time_record_list_item, parent, false);
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

    public class SurveyResponseViewHolder extends RecyclerView.ViewHolder {

        private TextView appName;
        private TextView activityId;
        private TextView activityWayToWellbeing;
        private TextView errorMessage;
        private ImageView image;

        public SurveyResponseViewHolder(@NonNull View itemView) {
            super(itemView);
            this.appName =  itemView.findViewById(R.id.nameTextView);
            this.activityId = itemView.findViewById(R.id.typeTextView);
            this.activityWayToWellbeing = itemView.findViewById(R.id.wayToWellbeingTextView);
            this.errorMessage = itemView.findViewById(R.id.error_message);
            this.image = itemView.findViewById(R.id.list_item_image);
        }

        public void onBind(AppItem response) {

            this.appName.setText(response.getAppName());
            this.activityId.setText(response.getActivityName());
            this.activityWayToWellbeing.setText(response.getWayToWellbeing());
            this.errorMessage.setVisibility(View.GONE);

            PackageManager packageManager = AppRecyclerViewAdapter.this.context.getPackageManager();
            Drawable appIcon;

            try {
                appIcon = packageManager.getApplicationIcon(response.getPackageName());
            } catch (PackageManager.NameNotFoundException e) {
                appIcon = ContextCompat.getDrawable(AppRecyclerViewAdapter.this.context, R.drawable.activity_type_app);
            }

            this.image.setImageDrawable(appIcon);
        }
    }
}
