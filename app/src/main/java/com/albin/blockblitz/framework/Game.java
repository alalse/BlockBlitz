package com.albin.blockblitz.framework;

import android.graphics.Canvas;
import android.util.Pair;
import android.view.MotionEvent;

import com.albin.blockblitz.enums.Statistic;
import com.albin.blockblitz.gameobjects.Block;
import com.albin.blockblitz.gameobjects.BlockHolder;
import com.albin.blockblitz.gameobjects.Board;
import com.albin.blockblitz.enums.GameAction;
import com.albin.blockblitz.gameobjects.GameMenu;
import com.albin.blockblitz.gameobjects.GameOverMenu;
import com.albin.blockblitz.interfaces.gameMenuListener;
import com.google.firebase.auth.FirebaseAuth;

public class Game {
    private Board board;
    private BlockHolder holder;
    private Block currentlyHolding;
    private GameMenu gameMenu;
    private GameOverMenu gameOverMenu;
    private final gameMenuListener gameMenuListener;
    private final FirebaseAuth auth;

    public Game(gameMenuListener gameMenuListener) {
        this.gameMenuListener = gameMenuListener;
        reset();

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            FirestoreHandler.incrementStatistic(Statistic.GAMES_PLAYED);
        }
    }

    public void update() {
        board.update();
        holder.update();
    }

    public void draw(Canvas canvas) {
        board.draw(canvas);
        holder.draw(canvas);

        if (currentlyHolding != null) {
            currentlyHolding.setScale(1f);
            currentlyHolding.draw(canvas);
        }

        gameMenu.draw(canvas);

        if (gameOverMenu.isActive()) {
            gameOverMenu.draw(canvas);
        }
    }

    //Reset game variables
    private void reset() {
        board = new Board();
        holder = new BlockHolder();
        currentlyHolding = null;
        gameMenu = new GameMenu();
        gameOverMenu = new GameOverMenu();
    }

    //Process a game action/a click on a menu item
    private void processGameAction(GameAction action) {
        if (action == GameAction.BACK_TO_MAIN_MENU) {
            if (auth.getCurrentUser() != null) {
                FirestoreHandler.updateScores(board.getScore());
                uploadStatistics();
            }
            gameMenuListener.callbackMethod();
        }
        else if (action == GameAction.RESTART) {
            if (auth.getCurrentUser() != null) {
                FirestoreHandler.updateScores(board.getScore());
                uploadStatistics();
            }
            reset();
        }
    }

    //Uploads statistics to the database
    private void uploadStatistics() {
        FirestoreHandler.updateDb();
    }

    //Handle the onTouchDownEvent
    public void onTouchDownEvent(MotionEvent event) {
        int clickedX = (int) event.getX();
        int clickedY = (int) event.getY();

        if (gameOverMenu.isActive()) {
            GameAction action = gameOverMenu.onTouchDownEvent(clickedX, clickedY);
            processGameAction(action);
        }
        else if (gameMenu.isActive()) {
            GameAction action = gameMenu.onTouchDownEvent(clickedX, clickedY);
            processGameAction(action);
        }
        else {
            if (holder.contains(clickedX, clickedY)) {
                currentlyHolding = holder.getBlock(clickedX, clickedY);
            }
            else if (gameMenu.contains(clickedX, clickedY)) {
                gameMenu.setActive(true);
            }
        }
    }

    //Handle the onTouchMoveEvent
    public void onTouchMoveEvent(MotionEvent event) {
        //If a block is held update the blocks position to a position under the user's finger
        if (currentlyHolding != null) {
            int newX = (int) event.getX() - (currentlyHolding.getWidth() / 2);
            int newY = (int) event.getY() - (int)(currentlyHolding.getHeight() * 1.2);
            currentlyHolding.setX(newX);
            currentlyHolding.setY(newY);
        }
    }

    //Handle the onTouchUpEvent
    public void onTouchUpEvent(MotionEvent event) {
        //if a block is held
        if (currentlyHolding != null) {

            //Get board index of touch up location and check if currently holding block can be placed at that position
            Pair<Integer, Integer> placementIndex = board.getBlockPlacementIndex(currentlyHolding);
            if (placementIndex != null && board.isBlockPlaceable(currentlyHolding, placementIndex)) {
                //Place currently holding block on the board and update board fill statistic
                board.placeBlock(currentlyHolding, placementIndex);

                //Add a new block the the block holder
                holder.generateNewBlock();

                //Remove any filled rows/columns from the grid after the addition of the new block
                int linesCleared = board.updateGrid();

                //Update statistics
                if (auth.getCurrentUser() != null) {
                    FirestoreHandler.updateBoardFill(board.getBoardFill());
                    FirestoreHandler.incrementStatistic(Statistic.LINES_CLEARED, linesCleared);
                }

                //Check if any of the blocks left in the block holder can fit in any
                //of the remaining positions on the board
                if (board.isGameOver(holder.getBlocks())) {
                    //Initialize game over
                    int score = board.getScore();
                    gameOverMenu.setScore(score);
                    gameOverMenu.setActive(true);
                }
            }
            //Currently holding block could not be placed at touch up location
            else {
                holder.addBlock(currentlyHolding);
            }

            //After touch up no block is held
            currentlyHolding = null;
        }
    }

    //Toggle game menu
    public void onBackButtonPressed() {
        gameMenu.setActive(!gameMenu.isActive());
    }
}