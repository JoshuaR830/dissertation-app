package com.joshuarichardson.fivewaystowellbeing.ui.wellbeing_support;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WellbeingSupportAdapter extends RecyclerView.Adapter<WellbeingSupportAdapter.WellbeingSupportViewHolder> {

    private final List<WellbeingSupportItem> supportItemList;
    private final Context context;
    LayoutInflater inflater;

    public WellbeingSupportAdapter(Context context, List<WellbeingSupportItem> supportItemList) {
        this.inflater = LayoutInflater.from(context);
        this.supportItemList = supportItemList;
        this.context = context;
    }

    @NonNull
    @Override
    // Set empty view holders
    public WellbeingSupportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = this.inflater.inflate(R.layout.wellbeing_support_list_item, parent, false);
        return new WellbeingSupportViewHolder(view);
    }

    @Override
    // Bind data to the view holders
    public void onBindViewHolder(@NonNull WellbeingSupportViewHolder holder, int position) {
        holder.onBind(this.supportItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.supportItemList.size();
    }

    public class WellbeingSupportViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView description;
        ImageView image;
        Button button;

        public WellbeingSupportViewHolder(@NonNull View itemView) {
            super(itemView);
            // Get all of the views to set values for
            this.title = itemView.findViewById(R.id.wellbeing_support_list_item_title);
            this.description = itemView.findViewById(R.id.wellbeing_support_list_item_description);
            this.image = itemView.findViewById(R.id.passtime_list_item_image);
            this.button = itemView.findViewById(R.id.wellbeing_support_list_item_button);
        }

        public void onBind(WellbeingSupportItem supportItem) {
            // Set the values for each of the views in the view holder
            this.title.setText(supportItem.getTitle());
            this.description.setText(supportItem.getDescription());
            this.image.setImageResource(supportItem.getImageResourceId());

            this.button.setOnClickListener(v -> {
                Intent webViewIntent = new Intent(WellbeingSupportAdapter.this.context, WellbeingSupportWebViewActivity.class);
                Bundle webViewBundle = new Bundle();
                webViewBundle.putString("url", supportItem.getWebsiteUrl());
                webViewIntent.putExtras(webViewBundle);
                WellbeingSupportAdapter.this.context.startActivity(webViewIntent);
            });
        }
    }

}
