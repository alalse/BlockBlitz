package com.albin.blockblitz.ui;

import com.albin.blockblitz.framework.GameView;
import com.albin.blockblitz.interfaces.gameMenuListener;
import com.google.firebase.perf.metrics.Trace;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class GameActivity extends AppCompatActivity implements gameMenuListener {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        gameView = new GameView(this);
        setContentView(gameView);

        Trace mainToGame = (Trace) getIntent().getExtras().get("trace");
        mainToGame.stop();

        //Handle for the backbutton
        this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                gameView.onBackButtonPressed();
            }
        });
    }

    //Callback from Game on 'Return to main menu' GameAction
    @Override
    public void callbackMethod() {
        Intent intent = new Intent(this , MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}