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
        View view = inflater.inflate(R.layout.insight_container, parent, false);
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

        private LinearLayout layout;

        View small_card;
        View large_card;

        TextView title;
        TextView info;

        public InsightsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.small_card = itemView.findViewById(R.id.small_insight);
            this.large_card = itemView.findViewById(R.id.large_insight);

        }

        public void onBind(InsightsItem insightsItem) {
            if(insightsItem.getColumnWidth() == 2) {
                this.title = this.large_card.findViewById(R.id.insight_title);
                this.info = this.large_card.findViewById(R.id.insight_description);
                this.layout = this.large_card.findViewById(R.id.insight_card_layout);
                this.large_card.setVisibility(View.VISIBLE);
                this.small_card.setVisibility(View.GONE);
            } else {
                this.title = this.small_card.findViewById(R.id.insight_title);
                this.info = this.small_card.findViewById(R.id.insight_description);
                this.layout = this.small_card.findViewById(R.id.insight_card_layout);
                this.small_card.setVisibility(View.VISIBLE);
                this.large_card.setVisibility(View.GONE);
            }


            View divider = this.layout.findViewById(R.id.insight_divider);

            if(insightsItem.getSpecialView() != null) {
                divider.setVisibility(View.VISIBLE);
                this.layout.addView(insightsItem.getSpecialView());
            } else {
                divider.setVisibility(View.GONE);
            }

            this.title.setText(insightsItem.getTitle());
            this.info.setText(insightsItem.getInfo());

        }
    }
}
