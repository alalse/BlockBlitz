package com.albin.blockblitz.gameobjects;

import android.graphics.Canvas;
import android.graphics.RectF;

public abstract class GameObject {
    protected int x, y;
    protected int width, height;

    public abstract void update();
    public abstract void draw(Canvas canvas);

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean contains(int clickedX, int clickedY) {
        return new RectF(x, y, x+width, y+height).contains(clickedX, clickedY);
    }
}
