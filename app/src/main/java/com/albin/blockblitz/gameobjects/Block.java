package com.albin.blockblitz.gameobjects;

import android.graphics.Canvas;
import android.graphics.Color;

import com.albin.blockblitz.enums.BlockProperty;
import com.albin.blockblitz.enums.BlockType;
import com.albin.blockblitz.framework.GameView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Block extends GameObject {
    private BlockSegment[][] segmentArray;
    private int segmentWidth;
    private final int color;
    private float scale = 1f;

    public Block(BlockType type) {
        int[][] sArray;

        //Get block color config and format it as json
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        String json = remoteConfig.getValue("block_colors")
                .asString().replace("=", ":");
        //Parse json and get color values for 'type'
        JsonObject colors = JsonParser.parseString(json).getAsJsonObject()
                .get(type.toString()).getAsJsonObject();
        //set color values for 'type'
        color = Color.rgb(colors.get("R").getAsInt(), colors.get("G").getAsInt(),
                colors.get("B").getAsInt());

        //Set block shape
        switch (type) {
            case SMALL_BLOCK:
                sArray = new int[][]{{1}};
                break;

            case MEDIUM_BLOCK:
                sArray = new int[][]{{1, 1},
                                     {1, 1}};
                break;

            case LARGE_BLOCK:
                sArray = new int[][]{{1, 1, 1},
                                     {1, 1, 1},
                                     {1, 1, 1}};
                break;

            case SHORT_LINE:
                sArray = new int[][]{{1, 1},
                                     {0, 0}};
                break;

            case NORMAL_LINE:
                sArray = new int[][]{{1, 1, 1},
                                     {0, 0, 0},
                                     {0, 0, 0}};
                break;

            case LONG_LINE:
                sArray = new int[][]{{1, 1, 1, 1},
                                     {0, 0, 0, 0},
                                     {0, 0, 0, 0},
                                     {0, 0, 0, 0}};
                break;

            case LONGEST_LINE:
                sArray = new int[][]{{1, 1, 1, 1, 1},
                                     {0, 0, 0, 0, 0},
                                     {0, 0, 0, 0, 0},
                                     {0, 0, 0, 0, 0},
                                     {0, 0, 0, 0, 0}};
                break;

            case SMALL_L:
                sArray = new int[][]{{1, 0},
                                     {1, 1}};
                break;

            case LARGE_L:
                sArray = new int[][]{{1, 0, 0},
                                     {1, 0, 0},
                                     {1, 1, 1}};
                break;

            case SMALL_T:
                sArray = new int[][]{{1, 1, 1},
                                     {0, 1, 0},
                                     {0, 0, 0}};
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        //Randomly rotate and mirror sArray
        sArray = rotateArray(sArray);

        //Remove empty rows and columns
        sArray = trimArray(sArray);

        //Convert int array 'sArray' into BlockSegment array 'segmentArray'
        convertArray(sArray);
    }

    //Rotates and mirrors content of sArray
    private int[][] rotateArray(int[][] sArray) {
        boolean rotate, mirrorHorizontally, mirrorVertically;
        Random rd = new Random();
        rotate = rd.nextBoolean();
        mirrorHorizontally = rd.nextBoolean();
        mirrorVertically = rd.nextBoolean();

        //Rotate content of sArray
        if (rotate) {
            ArrayList<int[]> rows = new ArrayList<>(Arrays.asList(sArray));
            sArray = new int[sArray.length][sArray.length];
            for (int y = 0; y < sArray.length; ++y) {
                for (int x = 0; x < sArray.length; ++x) {
                    sArray[x][y] = rows.get(y)[x];
                }
            }
        }

        //Mirror horizontally (up/down)
        if (mirrorHorizontally) {
            for (int i = 0; i < sArray.length/2; ++i) {
                int[] tmp = sArray[i];
                sArray[i] = sArray[sArray.length - i - 1];
                sArray[sArray.length - i - 1] = tmp;
            }
        }

        //Mirror vertically (left/right)
        if (mirrorVertically) {
            for (int[] row : sArray) {
                for (int i = 0; i < row.length/2; ++i) {
                    int tmp = row[i];
                    row[i] = row[row.length - i - 1];
                    row[row.length - i - 1] = tmp;
                }
            }
        }

        return sArray;
    }

    //Remove all empty rows and columns from sArray
    private int[][] trimArray(int[][] sArray) {
        //Copy all rows with segments from 'sArray'
        ArrayList<int[]> rows = new ArrayList<>();
        for (int[] row : sArray) {
            boolean foundSegment = false;
            for (int i : row) {
                if (i == 1) {
                    foundSegment = true;
                    break;
                }
            }

            if (foundSegment) {
                rows.add(row);
            }
        }

        //Construct array with content of 'rows'
        int[][] tmp = new int[rows.size()][sArray.length];
        for (int i = 0; i < rows.size(); ++i) {
            tmp[i] = rows.get(i);
        }

        //Copy all columns with segments from temporary array
        ArrayList<int[]> cols = new ArrayList<>();
        for (int x = 0; x < tmp[0].length; ++x) {
            boolean foundSegment = false;
            int[] col = new int[tmp.length];
            for (int y = 0; y < tmp.length; ++y) {
                col[y] = tmp[y][x];
                if (tmp[y][x] == 1) {
                    foundSegment = true;
                }
            }

            if (foundSegment) {
                cols.add(col);
            }
        }

        //Construct array with content of 'cols'
        tmp = new int[tmp.length][cols.size()];
        for (int x = 0; x < cols.size(); ++x) {
            for (int y = 0; y < tmp.length; ++y) {
                tmp[y][x] = cols.get(x)[y];
            }
        }

        return tmp;
    }

    //Convert sArray into an array of BlockSegments and assign it to
    //segmentArray array
    private void convertArray(int[][] sArray) {
        segmentArray = new BlockSegment[sArray.length][sArray[0].length];

        for (int i = 0; i < sArray.length; ++i) {
            for (int j = 0; j < sArray[0].length; ++j) {
                if (sArray[i][j] == 1) {
                    segmentArray[i][j] = new BlockSegmentBuilder().setColor(color)
                            .setBlockProperty(BlockProperty.NO_PROPERTY).build();
                }
            }
        }
    }

    @Override
    public void update() {}

    @Override
    public void draw(Canvas canvas) {
        segmentWidth = (int) ((GameView.width / 12) * scale);
        width = segmentArray[0].length * segmentWidth;
        height = segmentArray.length * segmentWidth;

        for (int i = 0; i < segmentArray.length; ++i) {
            for (int j = 0; j < segmentArray[0].length; ++j) {
                int left = x + segmentWidth * j;
                int top = y + segmentWidth * i;

                if (segmentArray[i][j] != null) {
                    segmentArray[i][j].setX(left);
                    segmentArray[i][j].setY(top);
                    segmentArray[i][j].setScale(scale);
                    segmentArray[i][j].draw(canvas);
                }
            }
        }
    }

    public BlockSegment[][] getSegments() {
        return segmentArray;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public int getWidth() {
        return segmentArray[0].length * segmentWidth;
    }

    public int getHeight() {
        return segmentArray.length * segmentWidth;
    }

    public int getSegmentWidth() { return segmentWidth; }
}