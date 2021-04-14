package com.albin.blockblitz.framework;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.albin.blockblitz.interfaces.gameMenuListener;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private final Game game;
    private GUIThread thread;

    public static int height, width;

    public GameView(Context context) {
        super(context);
        game = new Game((gameMenuListener) context);
        getHolder().addCallback(this);
        setFocusable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            game.onTouchDownEvent(event);
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            game.onTouchMoveEvent(event);
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            game.onTouchUpEvent(event);
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        height = getHeight();
        width = getWidth();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        thread = new GUIThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            game.draw(canvas);
        }
    }

    public void update() {
        game.update();
    }

    public void onBackButtonPressed() {
        game.onBackButtonPressed();
    }
}