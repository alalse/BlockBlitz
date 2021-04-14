package com.albin.blockblitz.ui;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.albin.blockblitz.R;
import com.albin.blockblitz.enums.GameState;
import com.albin.blockblitz.interfaces.mainMenuListener;
import com.google.firebase.auth.FirebaseAuth;

public class MainMenuFragment extends Fragment {
    private mainMenuListener mmlistener;

    public MainMenuFragment() {}

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mmlistener = (mainMenuListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        //Add functionality to the play button
        Button btn = view.findViewById(R.id.playBtn);
        btn.setOnClickListener(_view -> mmlistener.callbackMethod(GameState.GAME));

        //Add functionality to the account/statistics button
        btn = view.findViewById(R.id.accountBtn);
        btn.setOnClickListener(_view -> mmlistener.callbackMethod(GameState.ACCOUNT));

        //Add functionality to the leaderboards button
        btn = view.findViewById(R.id.leaderboardBtn);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            btn.setOnClickListener(_view -> mmlistener.callbackMethod(GameState.LEADERBOARD));
        }
        else {
            btn.setVisibility(View.GONE);
        }

        //Settings button
        btn = view.findViewById(R.id.settingsBtn);
        btn.setOnClickListener(_view -> mmlistener.callbackMethod(GameState.SETTINGS));

        return view;
    }
}