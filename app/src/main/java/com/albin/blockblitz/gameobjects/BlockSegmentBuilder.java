package com.albin.blockblitz.gameobjects;

import android.graphics.Color;

import com.albin.blockblitz.enums.BlockProperty;

public class BlockSegmentBuilder {
    private int color;
    BlockProperty property = BlockProperty.NO_PROPERTY;
    boolean dummy = false;

    BlockSegmentBuilder setColor(int color) {
        this.color = color;
        return this;
    }

    BlockSegmentBuilder setBlockProperty(BlockProperty property) {
        this.property = property;
        return this;
    }

    BlockSegmentBuilder setDummy(boolean dummy) {
        this.dummy = dummy;
        return this;
    }

    BlockSegment build() {
        if (dummy) {
            return new BlockSegment(Color.LTGRAY, property, true);
        }
        else {
            return new BlockSegment(color, property, false);
        }
    }
}