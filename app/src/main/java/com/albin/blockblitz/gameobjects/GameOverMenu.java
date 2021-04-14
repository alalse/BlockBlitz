package com.albin.blockblitz.gameobjects;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.albin.blockblitz.R;
import com.albin.blockblitz.enums.GameAction;
import com.albin.blockblitz.enums.Statistic;
import com.albin.blockblitz.framework.FirestoreHandler;
import com.albin.blockblitz.framework.ResourceLoader;

public class GameOverMenu extends GameMenuObject {
    private final RectF gameOverRect;
    private final RectF restart;
    private final RectF returnToMainMenu;
    private int score;

    public GameOverMenu() {
        super();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(70);

        gameOverRect = new RectF(0, 0, 0, 0);
        restart = new RectF(0, 0, 0, 0);
        returnToMainMenu = new RectF(0, 0, 0, 0);
    }

    @Override
    public void update() {}

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        p.setColor(Color.GRAY);
        p.setAlpha(225);
        int margin = x;
        int textMargin = (x/8) * 7;
        int top = y + margin;
        textPaint.setTextSize((margin / 10) * 7); //Random size calculation that according to my tests fits well and scales with screens

        gameOverRect.set(x + margin, top, x + width - margin, top + x * 3);
        canvas.drawRoundRect(gameOverRect, 10, 10, p);
        canvas.drawText(ResourceLoader.getString(R.string.game_over), gameOverRect.centerX(), top + textMargin, textPaint);
        canvas.drawText(ResourceLoader.getString(R.string.game_over_score, String.valueOf(score)), gameOverRect.centerX(), top + textMargin * 2, textPaint);
        String highscore = String.valueOf(FirestoreHandler.getStatistic(Statistic.HIGHSCORE));
        canvas.drawText(ResourceLoader.getString(R.string.game_over_highscore, highscore), gameOverRect.centerX(), top + textMargin * 3, textPaint);

        int smallMargin = x/12;
        top += x * 3 + margin;

        restart.set(x + margin, top + smallMargin, x + width - margin, top + x * 2 - smallMargin);
        canvas.drawRoundRect(restart, 10, 10, p);
        canvas.drawText(ResourceLoader.getString(R.string.restart_game), restart.centerX(), restart.centerY() + margin/4, textPaint);

        top += x + margin;

        returnToMainMenu.set(x + margin, top + smallMargin, x + width - margin, top + x * 2 - smallMargin);
        canvas.drawRoundRect(returnToMainMenu, 10, 10, p);
        canvas.drawText(ResourceLoader.getString(R.string.return_to_main_menu), returnToMainMenu.centerX(), returnToMainMenu.centerY() + margin/4, textPaint);
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
        }

        return null;
    }

    public void setScore(int score) { this.score = score; }
}