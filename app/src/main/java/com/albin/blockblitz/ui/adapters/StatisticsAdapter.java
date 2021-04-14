package com.albin.blockblitz.ui.adapters;

import android.util.Pair;

import androidx.annotation.NonNull;

import com.albin.blockblitz.enums.Statistic;
import com.albin.blockblitz.framework.FirestoreHandler;

import java.util.ArrayList;

public class StatisticsAdapter extends BaseAdapter {
    private final ArrayList<Pair<String, Integer>> statistics;

    public StatisticsAdapter(String[] statisticsNames) {
        statistics = new ArrayList<>();
        Statistic[] tmp = Statistic.values();
        int count = 0;

        for (String index : statisticsNames) {
            statistics.add(new Pair<>(index, FirestoreHandler.getStatistic(tmp[count])));
            ++count;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String index = statistics.get(position).first;
        holder.getIndexView().setText(index + ": ");

        if (index.equals("Average board fill")) {
            holder.getScoreView().setText(statistics.get(position).second + "%");
        } else {
            holder.getScoreView().setText(String.valueOf(statistics.get(position).second));
        }
    }

    @Override
    public int getItemCount() {
        return statistics.size();
    }
}