/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 See AUTHORS file
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.mini2Dx.tictactoe;

import org.mini2Dx.core.game.BasicGame;
import org.mini2Dx.core.geom.Rectangle;
import org.mini2Dx.core.graphics.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import org.mini2Dx.tictactoe.model.Game;

/**
 * The main game logic
 */
public class TicTacToeGame extends BasicGame {
	public static final String GAME_IDENTIFIER = "org.mini2Dx.tictactoe";

	private Game gm = new Game();

	// Use GlyphLayout to calculate the size of rendered text
	private final GlyphLayout glyphLayout = new GlyphLayout();

	// Stores where the board should be rendered
	private int boardOffsetX, boardOffsetY;
	// Stores the size each space on the board should be rendered at
	private int spaceSize;

	private boolean warmingUp = true;
	// Timer for starting the game
	private float startTimer = 5f;
	// Store if the victory has been seen
	private boolean victoryAcknowledged = false;

	@Override
	public void initialise() {
		// Calculate the board position and size - we'll place it center screen
		spaceSize = Math.min(getWidth() / 4, getHeight() / 4);
		boardOffsetX = (getWidth() / 2) - ((spaceSize * 3) / 2);
		boardOffsetY = (getHeight() / 2) - ((spaceSize * 3) / 2);
	}

	@Override
	public void update(float delta) {
		if (warmingUp) {
			victoryAcknowledged = false;
			startTimer -= delta;
			if (startTimer <= 0f) {
				warmingUp = false;
				// Reset the timer for the next game
				startTimer = 5f;
			}
			else return;
		}
		if (gm.finished()) {
			//As the game runs as 60FPS, the screen may still be touched from the previous frame
			if (!victoryAcknowledged) {
				if(!Gdx.input.isTouched()) {
					victoryAcknowledged = true;
				}
				return;
			}
			// Wait for a touch or click to start a new game
			if (Gdx.input.isTouched()) {
				gm.reset();
				warmingUp = true;
			}
		}
		else {
			// Wait for the player to touch/click the screen
			if (!Gdx.input.isTouched()) {
				return;
			}
			handleInput(Gdx.input.getX(), Gdx.input.getY());
		}
	}

	@Override
	public void interpolate(float alpha) {
		// No moving sprites so this method does nothing
	}

	@Override
	public void render(Graphics g) {
		g.setBackgroundColor(Color.WHITE);

		renderBoard(g);
		if (warmingUp) {
			// Render a countdown
			g.setColor(Color.BLACK);
			renderMessage(g, "Starting game in " + MathUtils.round(startTimer) + " seconds...");
			return;
		}
		switch (gm.state()) {
		case PLAYER_1_TURN:
			g.setColor(Color.BLUE);
			renderMessage(g, "Player 1's turn");
			break;
		case PLAYER_1_VICTORY:
			g.setColor(Color.GREEN);
			renderMessage(g, "Player 1 wins!");
			break;
		case PLAYER_2_TURN:
			g.setColor(Color.PURPLE);
			renderMessage(g, "Player 2's turn");
			break;
		case PLAYER_2_VICTORY:
			g.setColor(Color.GREEN);
			renderMessage(g, "Player 2 wins!");
			break;
		case TIED:
			g.setColor(Color.BLACK);
			renderMessage(g, "Game is tied :(");
			break;
		}
	}

	/**
	 * Handles player input
	 * 
	 * @param touchX
	 *            The x coordinate that was touched
	 * @param touchY
	 *            The y coordinate that was touched
	 * @return True if the board changed
	 */
	private boolean handleInput(int touchX, int touchY) {
		Rectangle rectangle = new Rectangle();

		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				// Check to see if the board space was touched
				rectangle.set(boardOffsetX + (x * spaceSize), boardOffsetY + (y * spaceSize), spaceSize, spaceSize);
				if (!rectangle.contains(touchX, touchY)) {
					continue;
				}
				if (gm.mark(x, y))
					return true;
			}
		}
		return false;
	}

	/**
	 * Renders a message at the top center of the screen
	 * 
	 * @param g
	 *            The {@link Graphics} context
	 * @param message
	 *            The message to render
	 */
	private void renderMessage(Graphics g, String message) {
		glyphLayout.setText(g.getFont(), message);
		int renderX = MathUtils.round((getWidth() / 2) - (glyphLayout.width / 2f));
		int renderY = MathUtils.round(glyphLayout.height);
		g.drawString(message, renderX, renderY);
	}

	/**
	 * Renders the game board
	 * 
	 * @param g
	 *            The {@link Graphics} context
	 */
	private void renderBoard(Graphics g) {
		g.setLineHeight(4);
		g.setColor(Color.BLACK);

		int quarterSpaceSize = (spaceSize / 4);
		int halfSpaceSize = (spaceSize / 2);

		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				int renderX = boardOffsetX + (x * spaceSize);
				int renderY = boardOffsetY + (y * spaceSize);
				g.drawRect(renderX, renderY, spaceSize, spaceSize);

				switch (gm.board(x, y)) {
				case O:
					g.drawCircle(renderX + halfSpaceSize, renderY + halfSpaceSize, quarterSpaceSize);
					break;
				case X:
					g.drawLineSegment(renderX + quarterSpaceSize, renderY + quarterSpaceSize,
							renderX + quarterSpaceSize + halfSpaceSize, renderY + quarterSpaceSize + halfSpaceSize);
					g.drawLineSegment(renderX + halfSpaceSize + quarterSpaceSize, renderY + quarterSpaceSize,
							renderX + quarterSpaceSize, renderY + quarterSpaceSize + halfSpaceSize);
					break;
				case FREE:
				default:
					break;
				}
			}
		}
	}
}
