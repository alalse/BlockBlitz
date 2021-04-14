package com.albin.blockblitz.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.albin.blockblitz.R;

public abstract class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.ViewHolder> {
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public abstract void onBindViewHolder(@NonNull ViewHolder holder, final int position);

    @Override
    public abstract int getItemCount();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView indexView, scoreView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            indexView = itemView.findViewById(R.id.leaderboardIndexView);
            scoreView = itemView.findViewById(R.id.leaderboardScoreView);
        }

        public TextView getIndexView() {
            return indexView;
        }

        public TextView getScoreView() {
            return scoreView;
        }
    }
}