package com.joshuarichardson.fivewaystowellbeing.ui.insights;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joshuarichardson.fivewaystowellbeing.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InsightsAdapter extends RecyclerView.Adapter<InsightsAdapter.InsightsViewHolder> {

    private final List<InsightsItem> insightsList;
    LayoutInflater inflater;

    public InsightsAdapter(Context context, List<InsightsItem> insights) {
        this.inflater = LayoutInflater.from(context);
        Log.d("insights list", String.valueOf(insights.size()));
        this.insightsList = insights;
    }

    @NonNull
    @Override
    public InsightsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.insight_item, parent, false);
        return new InsightsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InsightsViewHolder holder, int position) {
        Log.d("size", String.valueOf(this.insightsList.size()));
        Log.d("position", String.valueOf(position));
        Log.d("item", String.valueOf(this.insightsList.get(position).getTitle()));
        holder.onBind(this.insightsList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.insightsList.size();
    }

    public class InsightsViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout layout;
        TextView title;
        TextView info;

        public InsightsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.layout = itemView.findViewById(R.id.insight_card_layout);
            this.title = itemView.findViewById(R.id.insight_title);
            this.info = itemView.findViewById(R.id.insight_description);
        }

        public void onBind(InsightsItem insightsItem) {
            Log.d("Hello", "Hi");
            this.title.setText(insightsItem.getTitle());
            this.info.setText(insightsItem.getInfo());

            if(insightsItem.getSpecialView() != null) {
                this.layout.addView(insightsItem.getSpecialView());
            }
        }
    }
}
