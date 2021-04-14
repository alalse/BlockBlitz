package com.albin.blockblitz.ui.adapters;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class PrivateLeaderboardAdapter extends BaseAdapter {
    private final ArrayList<Long> scores;

    public PrivateLeaderboardAdapter(ArrayList<Long> scores) {
        this.scores = scores;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (position < 9) {
            holder.getIndexView().setText("  " + (position+1) + ": ");
        }
        else {
            holder.getIndexView().setText((position+1) + ": ");
        }

        holder.getScoreView().setText(String.valueOf(scores.get(position)));
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }
}