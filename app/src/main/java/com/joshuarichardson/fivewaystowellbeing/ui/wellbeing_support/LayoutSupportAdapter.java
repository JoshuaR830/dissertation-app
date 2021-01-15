package com.joshuarichardson.fivewaystowellbeing.ui.wellbeing_support;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LayoutSupportAdapter extends RecyclerView.Adapter<LayoutSupportAdapter.WellbeingSupportViewHolder> {

    public LayoutSupportAdapter() {
        // Need to work out how to test this
        // Need to check that the view contains all of the required items
        // For given items does it display this, that...
    }

    @NonNull
    @Override
    public WellbeingSupportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull WellbeingSupportViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class WellbeingSupportViewHolder extends RecyclerView.ViewHolder {
        public WellbeingSupportViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
