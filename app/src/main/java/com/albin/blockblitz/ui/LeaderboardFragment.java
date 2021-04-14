package com.albin.blockblitz.ui;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.albin.blockblitz.R;
import com.albin.blockblitz.enums.ResponseCode;
import com.albin.blockblitz.framework.FirestoreHandler;
import com.albin.blockblitz.ui.adapters.GlobalLeaderboardAdapter;
import com.albin.blockblitz.ui.adapters.PrivateLeaderboardAdapter;

public class LeaderboardFragment extends BaseFragment {
    private RecyclerView rView;

    public LeaderboardFragment() {}
    public static LeaderboardFragment newInstance() { return new LeaderboardFragment(); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        rView = view.findViewById(R.id.leaderboardView);
        rView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rView.setAdapter(new PrivateLeaderboardAdapter(FirestoreHandler.getPrivateLeaderboard()));

        Button btn = view.findViewById(R.id.privateLeaderboard);
        btn.setOnClickListener(_view -> rView.setAdapter(new PrivateLeaderboardAdapter(FirestoreHandler.getPrivateLeaderboard())));

        btn = view.findViewById(R.id.globalLeaderboard);
        btn.setOnClickListener(_view -> FirestoreHandler.getGlobalLeaderboardFromDb().observe(this, responseCode -> {
            if (responseCode == ResponseCode.SUCCESS) {
                rView.setAdapter(new GlobalLeaderboardAdapter(FirestoreHandler.getGlobalLeaderboard()));
            }
            else if (responseCode == ResponseCode.FAILURE) {
                Toast.makeText(getActivity(), R.string.get_global_leaderboard_failure, Toast.LENGTH_SHORT).show();
            }
        }));

        return view;
    }
}