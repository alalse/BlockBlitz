package com.albin.blockblitz.gameobjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Pair;

import com.albin.blockblitz.enums.BlockProperty;
import com.albin.blockblitz.framework.GameView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class Board extends GameObject {
    private final BlockSegment[][] segmentArray;
    private final Paint p;
    private int score;

    public Board() {
        segmentArray = new BlockSegment[10][10];
        p = new Paint();
        p.setColor(Color.LTGRAY);
        p.setStyle(Paint.Style.FILL);
        p.setTextAlign(Paint.Align.CENTER);

        //Fill board with empty segments
        //(Segments will have invalid positions until first update call)
        for (int i = 0; i < segmentArray.length; ++i) {
            for (int j = 0; j < segmentArray[0].length; ++j) {
                segmentArray[j][i] = new BlockSegmentBuilder()
                        .setBlockProperty(BlockProperty.NO_PROPERTY)
                        .setDummy(true).build();
            }
        }
    }

    @Override
    public void update() {}

    @Override
    public void draw(Canvas canvas) {
        x = GameView.width / 12;
        y = GameView.height / 8;
        p.setTextSize(y/2);

        //Draw score
        canvas.drawText(String.valueOf(score), GameView.width / 2, y, p);

        //Draw board
        for (int i = 0; i < segmentArray.length; ++i) {
            for (int j = 0; j < segmentArray[0].length; ++j) {
                int left = x * (j + 1);
                int top = y + x * (i + 1);
                segmentArray[j][i].setX(left);
                segmentArray[j][i].setY(top);
                segmentArray[j][i].draw(canvas);
            }
        }
    }

    //Update board by removing filled rows/columns
    public int updateGrid() {
        ArrayList<Pair<Integer, Integer>> toBeRemoved = new ArrayList<>();
        int linesCleared = 0;

        //Get filled rows
        for (int i = 0; i < segmentArray.length; ++i) {
            ArrayList<Pair<Integer, Integer>> row = new ArrayList<>();
            boolean remove = true;

            for (int j = 0; j < segmentArray[0].length; ++j) {
                row.add(new Pair<>(j, i));

                if (segmentArray[j][i].isDummy()) {
                    remove = false;
                    break;
                }
            }

            if (remove) {
                linesCleared += row.size() / 10;
                toBeRemoved.addAll(row);
                ++score;
            }
        }

        //Get filled columns
        for (int j = 0; j < segmentArray[0].length; ++j) {
            ArrayList<Pair<Integer, Integer>> col = new ArrayList<>();
            boolean remove = true;

            for (int i = 0; i < segmentArray.length; ++i) {
                col.add(new Pair<>(j, i));

                if (segmentArray[j][i].isDummy()) {
                    remove = false;
                    break;
                }
            }

            if (remove) {
                linesCleared += col.size() / 10;
                toBeRemoved.addAll(col);
                ++score;
            }
        }

        //Remove duplicate pairs
        Set<Pair<Integer, Integer>> set = new LinkedHashSet<>(toBeRemoved);
        toBeRemoved = new ArrayList<>(set);

        //Reset specified segments on segmentArray
        for (Pair<Integer, Integer> p : toBeRemoved) {
            segmentArray[p.first][p.second] = new BlockSegmentBuilder()
                    .setBlockProperty(BlockProperty.NO_PROPERTY)
                    .setDummy(true).build();
        }

        return linesCleared;
    }

    //Gets the board indexes corresponding to the clicked position
    public Pair<Integer, Integer> getBlockPlacementIndex(Block b) {
        int bx = b.x + b.getSegmentWidth() / 2; //current raw position
        int by = b.y + b.getSegmentWidth() / 2;

        //linear search
        for (int i = 0; i < segmentArray.length; ++i) {
            for (int j = 0; j < segmentArray[0].length; ++j) {
                if (segmentArray[i][j].contains(bx, by)) {
                    return new Pair<>(i, j);
                }
            }
        }

        return null;
    }

    //Check if a given block is placeable on a given position
    public boolean isBlockPlaceable(Block b, Pair<Integer, Integer> pos) {
        BlockSegment[][] segments = b.getSegments();
        int blockWidth = segments[0].length;
        int blockHeight = segments.length;

        //Check for out of bounds
        if (pos.first + blockWidth > segmentArray[0].length ||
            pos.second + blockHeight > segmentArray.length) {
            return false;
        }

        //Check for collisions
        boolean placeable = true;
        int blockJ = 0;
        for (int i = pos.second; i < pos.second + segments.length; ++i) {
            int blockI = 0;
            for (int j = pos.first; j < pos.first + segments[0].length; ++j) {
                if (!segmentArray[j][i].isDummy() && segments[blockJ][blockI] != null) {
                    placeable = false;
                    break;
                }
                ++blockI;
            }
            ++blockJ;
        }
        return placeable;
    }

    //Place given block on the given position
    public void placeBlock(Block b, Pair<Integer, Integer> pos) {
        BlockSegment[][] segments = b.getSegments();
        int blockJ = 0;
        for (int i = pos.second; i < pos.second + segments.length; ++i) {
            int blockI = 0;
            for (int j = pos.first; j < pos.first + segments[0].length; ++j) {
                if (segments[blockJ][blockI] != null) {
                    segmentArray[j][i] = segments[blockJ][blockI];
                }
                ++blockI;
            }
            ++blockJ;
        }
    }

    //Check if any of the given blocks can be placed anywhere on the board
    public boolean isGameOver(ArrayList<Block> blocks) {
        boolean gameOver = true;
        for (Block b : blocks) {
            for (int arrayY = 0; arrayY < segmentArray.length; ++arrayY) {
                for (int arrayX = 0; arrayX < segmentArray[0].length; ++arrayX) {
                    if (isBlockPlaceable(b, new Pair<>(arrayX, arrayY))) {
                        gameOver = false;
                        break;
                    }
                }
            }
        }

        return gameOver;
    }

    //Get the percentage of the that is filled with blocks
    public int getBoardFill() {
        int total = 0;
        int filled = 0;
        for (BlockSegment[] bArray : segmentArray) {
            for (BlockSegment b : bArray) {
                total++;
                if (!b.isDummy()) {
                    filled++;
                }
            }
        }

        return (total + filled) / 2;
    }

    public int getScore() { return score; }
}