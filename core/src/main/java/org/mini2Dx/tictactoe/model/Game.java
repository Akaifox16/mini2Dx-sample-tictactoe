package org.mini2Dx.tictactoe.model;

import com.badlogic.gdx.Gdx;

public class Game {
    // Stores the spaces on the game board as a 2D array
    private final Space[][] board = new Space[3][3];
    // Stores the state of the game
    private GameState state;


    public Game() {
        reset();
    }

    public void reset() {
        state = GameState.PLAYER_1_TURN;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                board[x][y] = Space.FREE;
            }
        }
    }

    public GameState state() {
        return state;
    }

    public Space board(int x, int y) {
        return board[x][y];
    }

    public boolean finished() {
        return state == GameState.PLAYER_1_VICTORY || state == GameState.PLAYER_2_VICTORY || state == GameState.TIED;
    }

    public boolean mark(int x, int y) {
        boolean isPlayer1 = state == GameState.PLAYER_1_TURN;
        // Ensure it is a free space
        if (board[x][y] != Space.FREE) {
            return false;
        }
        if (isPlayer1) {
            board[x][y] = Space.X;
        } else {
            board[x][y] = Space.O;
        }
        if (!isVictory(isPlayer1)) {
            if(isTied()) {
                state = GameState.TIED;
            } else {
                // Game is not won or tied, go to next turn
                if (isPlayer1) {
                    state = GameState.PLAYER_2_TURN;
                } else {
                    state = GameState.PLAYER_1_TURN;
                }
            }
            return true;
        }
        if (isPlayer1) {
            state = GameState.PLAYER_1_VICTORY;
        } else {
            state = GameState.PLAYER_2_VICTORY;
        }
        return true;
    }

    /**
     * Checks if a player has won the game
     * @param isPlayer1 True if we're checking for player 1's victory
     * @return True if the player won the game
     */
    private boolean isVictory(boolean isPlayer1) {
        Space search = isPlayer1 ? Space.X : Space.O;

        // Check rows
        for (int y = 0; y < 3; y++) {
            if (board[0][y] != search) {
                continue;
            }
            if (board[1][y] != search) {
                continue;
            }
            if (board[2][y] != search) {
                continue;
            }
            return true;
        }

        // Check columns
        for (int x = 0; x < 3; x++) {
            if (board[x][0] != search) {
                continue;
            }
            if (board[x][1] != search) {
                continue;
            }
            if (board[x][2] != search) {
                continue;
            }
            return true;
        }

        // Check diagonally
        if (board[0][0] == search && board[1][1] == search && board[2][2] == search) {
            return true;
        }
        if (board[0][2] == search && board[1][1] == search && board[2][0] == search) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the game is tied (i.e. the board is full)
     * @return True if the game is tied
     */
    private boolean isTied() {
        for(int x = 0; x < 3; x++) {
            for(int y = 0; y < 3; y++) {
                if(board[x][y] == Space.FREE) {
                    return false;
                }
            }
        }
        return true;
    }
}
