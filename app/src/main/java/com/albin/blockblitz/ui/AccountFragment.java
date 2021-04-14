package com.albin.blockblitz.ui;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.albin.blockblitz.R;
import com.albin.blockblitz.framework.FirestoreHandler;
import com.albin.blockblitz.ui.adapters.StatisticsAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class AccountFragment extends BaseFragment {
    private CreateAccountFragment caf;

    public AccountFragment() {}

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            ConstraintLayout cl = view.findViewById(R.id.loggedIn);
            cl.setVisibility(View.GONE);
            cl = view.findViewById(R.id.notLoggedIn);
            cl.setVisibility(View.VISIBLE);

            Button btn = view.findViewById(R.id.createOrLoginBtn);
            btn.setOnClickListener(_view -> {
                if (caf == null) {caf = CreateAccountFragment.newInstance(); }
                Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentHolder, caf)
                        .addToBackStack(null)
                        .commit();
            });
        }
        else {
            ConstraintLayout cl = view.findViewById(R.id.loggedIn);
            cl.setVisibility(View.VISIBLE);
            cl = view.findViewById(R.id.notLoggedIn);
            cl.setVisibility(View.GONE);

            TextView textView = view.findViewById(R.id.welcomeUserView);
            textView.setText(getString(R.string.welcome_user, FirestoreHandler.getUsername()));

            Button btn = view.findViewById(R.id.logoutBtn);
            btn.setOnClickListener(_view -> {
                auth.signOut();

                LinearLayout highscoreBox = getActivity().findViewById(R.id.highscoreBox);
                highscoreBox.setVisibility(View.INVISIBLE);

                getActivity().getSupportFragmentManager() //Reload fragment
                        .beginTransaction()
                        .detach(AccountFragment.this)
                        .attach(AccountFragment.this)
                        .commitNow();
            });

            RecyclerView rView = view.findViewById(R.id.statisticsView);
            rView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rView.setAdapter(new StatisticsAdapter(getResources().getStringArray(R.array.statistics)));
        }

        return view;
    }
}