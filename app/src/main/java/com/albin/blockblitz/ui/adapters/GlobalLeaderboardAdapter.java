package com.albin.blockblitz.ui.adapters;

import android.util.Pair;

import androidx.annotation.NonNull;

import com.albin.blockblitz.R;
import com.albin.blockblitz.framework.ResourceLoader;

import java.util.ArrayList;

public class GlobalLeaderboardAdapter extends BaseAdapter {
    private final ArrayList<Pair<String, Long>> scores;

    public GlobalLeaderboardAdapter(ArrayList<Pair<String, Long>> scores) {
        this.scores = scores;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getIndexView().setText(ResourceLoader.getString(R.string.global_leaderboard_index, scores.get(position).first));
        holder.getScoreView().setText(String.valueOf(scores.get(position).second));
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }
}