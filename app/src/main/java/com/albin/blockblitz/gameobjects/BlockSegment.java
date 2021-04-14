package com.albin.blockblitz.gameobjects;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.albin.blockblitz.enums.BlockProperty;
import com.albin.blockblitz.framework.GameView;

public class BlockSegment extends GameObject {
    //private final BlockProperty property;
    private final Paint p;
    private final boolean dummy;
    private float scale = 1f;
    private final RectF segment;

    public BlockSegment(int color, BlockProperty property, boolean dummy) {
        p = new Paint();
        p.setColor(color);
        //this.property = property;
        this.dummy = dummy; //a segment with dummy = true is the same as an empty position in a normal array
        segment = new RectF(0, 0, 0, 0);
    }

    @Override
    public void update() {}

    @Override
    public void draw(Canvas canvas) {
        int margin = (int) (5 * scale);
        width = height = (int) ((GameView.width / 12) * scale);

        int left = x+margin;
        int top = y+margin;
        int right = x+width-margin;
        int bottom = y+width-margin;

        segment.set(left, top, right, bottom);
        canvas.drawRoundRect(segment, 10, 10, p);
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public boolean isDummy() {
        return dummy;
    }
}
