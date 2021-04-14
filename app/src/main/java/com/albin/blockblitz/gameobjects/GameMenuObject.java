package com.albin.blockblitz.gameobjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.albin.blockblitz.enums.GameAction;
import com.albin.blockblitz.framework.GameView;

public abstract class GameMenuObject extends GameObject {
    protected final Paint p;
    protected final Paint textPaint;
    protected boolean active = false;
    private final RectF background;

    public GameMenuObject() {
        p = new Paint();
        textPaint = new Paint();
        background = new RectF(0, 0, 0, 0);
    }

    @Override
    public void update() {}

    @Override
    public void draw(Canvas canvas) {
        x = GameView.width / 12;
        y = GameView.height / 6;
        width =  x * 10;
        height = (int) (y * 2.5);

        //Draw menu background
        p.setColor(Color.WHITE);
        p.setAlpha(90);
        background.set(x, y, x + width, y + height);
        canvas.drawRoundRect(background, 10, 10, p);
    }

    public void setActive(boolean active) { this.active = active; }

    public boolean isActive() { return active; }

    public abstract GameAction onTouchDownEvent(int clickedX, int clickedY);
}