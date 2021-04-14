package com.albin.blockblitz.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.albin.blockblitz.R;
import com.albin.blockblitz.enums.ResponseCode;
import com.albin.blockblitz.enums.Statistic;
import com.albin.blockblitz.framework.FirestoreHandler;
import com.albin.blockblitz.enums.GameState;
import com.albin.blockblitz.interfaces.backButtonListener;
import com.albin.blockblitz.interfaces.mainMenuListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements mainMenuListener, backButtonListener {
    private MainMenuFragment mf;
    private AccountFragment af;
    private LeaderboardFragment lf;
    private SettingsFragment sf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);

        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                FirestoreHandler.getUserData(firebaseAuth.getUid()).observe(this, responseCode -> {
                    if (responseCode == ResponseCode.SUCCESS) {
                        TextView highscore = findViewById(R.id.mainActivityHighscore);
                        highscore.setText(String.valueOf(FirestoreHandler.getStatistic(Statistic.HIGHSCORE)));
                        LinearLayout highscoreBox = findViewById(R.id.highscoreBox);
                        highscoreBox.setVisibility(View.VISIBLE);
                    }
                    else if (responseCode == ResponseCode.FAILURE) {
                        Toast.makeText(this, R.string.get_user_data_failure, Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                });
            }
            else {
                LinearLayout highscoreBox = findViewById(R.id.highscoreBox);
                highscoreBox.setVisibility(View.INVISIBLE);
            }
        });

        if (mf == null) { mf = MainMenuFragment.newInstance(); }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentHolder, mf)
                .commit();
    }

    //removes animation when switching app language
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void callbackMethod(GameState state) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (state) {
            case GAME:
                Trace mainToGameTrace = FirebasePerformance.getInstance().newTrace("Main_to_Game");
                mainToGameTrace.start();

                Intent intent = new Intent(this , GameActivity.class);
                intent.putExtra("trace", mainToGameTrace);
                startActivity(intent);
                overridePendingTransition(0, 0);
                break;

            case ACCOUNT:
                if (af == null) {af = AccountFragment.newInstance(); }
                ft.replace(R.id.fragmentHolder, af)
                        .addToBackStack("account")
                        .commit();
                break;

            case LEADERBOARD:
                if (lf == null) { lf = LeaderboardFragment.newInstance(); }
                ft.replace(R.id.fragmentHolder, lf)
                        .addToBackStack(null)
                        .commit();
                break;

            case SETTINGS:
                if (sf == null) { sf = SettingsFragment.newInstance(); }
                ft.replace(R.id.fragmentHolder, sf)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }

    @Override
    public void backButtonCallbackMethod() {
        getSupportFragmentManager().popBackStackImmediate();
    }
}