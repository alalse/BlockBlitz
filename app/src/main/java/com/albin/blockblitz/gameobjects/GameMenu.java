package com.albin.blockblitz.gameobjects;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.albin.blockblitz.R;
import com.albin.blockblitz.enums.GameAction;
import com.albin.blockblitz.framework.GameView;
import com.albin.blockblitz.framework.ResourceLoader;

public class GameMenu extends GameMenuObject {
    private final RectF restart;
    private final RectF returnToMainMenu;
    private final RectF returnToGame;
    private final Bitmap gameMenuImage;

    public GameMenu() {
        super();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(70);
        gameMenuImage = ResourceLoader.getBitmap(R.drawable.cog);

        restart = new RectF(0, 0, 0, 0);
        returnToMainMenu = new RectF(0, 0, 0, 0);
        returnToGame = new RectF(0, 0, 0, 0);
    }

    @Override
    public void update() {}

    @Override
    public void draw(Canvas canvas) {
        if (active) {
            super.draw(canvas);
            int margin = x;
            int top = y + margin;
            int rectHeight = x * 2;
            int textSize = (margin / 10) * 7; //Random size calculation that according to my tests fits well and scales with screens
            textPaint.setTextSize(textSize);
            p.setColor(Color.GRAY);

            returnToGame.set(x + margin, top, x + width - margin, top + rectHeight);
            canvas.drawRoundRect(returnToGame, 10, 10, p);
            canvas.drawText(ResourceLoader.getString(R.string.resume_game), returnToGame.centerX(), returnToGame.centerY() + margin/4, textPaint);

            top += rectHeight + margin;

            restart.set(x + margin, top, x + width - margin, top + rectHeight);
            canvas.drawRoundRect(restart, 10, 10, p);
            canvas.drawText(ResourceLoader.getString(R.string.restart_game), restart.centerX(), restart.centerY() + margin/4, textPaint);

            top += rectHeight + margin;

            returnToMainMenu.set(x + margin, top, x + width - margin, top + rectHeight);
            canvas.drawRoundRect(returnToMainMenu, 10, 10, p);
            String tmp = ResourceLoader.getString(R.string.return_to_main_menu);
            if (tmp.length() > 20) { //scale text so that it does'nt overflow
                textPaint.setTextSize(textSize - (tmp.length() - 15));
            }
            canvas.drawText(tmp, returnToMainMenu.centerX(), returnToMainMenu.centerY() + margin/4, textPaint);
        }
        else {
            x = (GameView.width / 10) * 8;
            y = GameView.width / 10;
            width = height = y;
            canvas.drawBitmap(Bitmap.createScaledBitmap(gameMenuImage, y, y, true), x, y, p);
        }
    }

    @Override
    public GameAction onTouchDownEvent(int clickedX, int clickedY) {
        if (contains(clickedX, clickedY)) {
            if (returnToMainMenu.contains(clickedX, clickedY)) {
                return GameAction.BACK_TO_MAIN_MENU;
            }
            else if (restart.contains(clickedX, clickedY)) {
                return GameAction.RESTART;
            }
            else if (returnToGame.contains(clickedX, clickedY)) {
                active = false;
            }
        }
        else {
            active = false;
        }

        return null;
    }
}