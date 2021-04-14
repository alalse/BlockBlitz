package com.albin.blockblitz.gameobjects;

import android.graphics.Canvas;

import com.albin.blockblitz.enums.BlockType;
import com.albin.blockblitz.framework.GameView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlockHolder extends GameObject {
    private final ArrayList<Block> blocks;
    private final Random rd;
    private final List<BlockType> blockTypes;

    public BlockHolder() {
        blocks = new ArrayList<>();
        rd = new Random();

        //Get all block types from BlockType enum
        blockTypes = Collections.unmodifiableList(Arrays.asList(BlockType.values()));

        for (int i = 0; i < 3; ++i) {
            generateNewBlock();
        }
    }

    @Override
    public void update() {}

    @Override
    public void draw(Canvas canvas) {
        x = GameView.width / 12;
        y = (GameView.height / 10) * 7;
        width = GameView.width - (x * 2);
        height = x * 5;

        int i = 0;
        for (Block b : blocks) {
            int centeredX = x + ((width / 3) * i) + (width / 6) - (b.getWidth() / 2);
            int centeredY = y + (height / 2) - b.getHeight() / 2;

            b.setX(centeredX);
            b.setY(centeredY);
            b.setScale(0.75f);
            b.draw(canvas);
            ++i;
        }
    }

    //Add a block to block holder list
    public void addBlock(Block b) {
        blocks.add(b);
    }

    //Get the block at the clicked position
    public Block getBlock(int clickedX, int clickedY) {
        for (int i = 0; i < blocks.size(); ++i) {
            if (blocks.get(i).contains(clickedX, clickedY)) {
                return blocks.remove(i);
            }
        }

        return null;
    }

    //Get all blocks in block holder list
    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    //Generate a random new block and add it to block holder list
    public void generateNewBlock() {
        blocks.add(new Block(blockTypes.get(rd.nextInt(blockTypes.size()))));
    }
}