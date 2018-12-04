package levels;

import java.util.ArrayList;

import gameElements.Wall;
import gui.GameCanvas;

/**
 * 
 * @author Trent
 *
 */
public class Level {
	protected static int X_BORDER_SIZE = 30;
	protected static int Y_BORDER_SIZE = 30;
	protected static int SCREEN_WIDTH = 1280;
	protected static int SCREEN_HEIGHT = 720 - GameCanvas.HUD_HEIGHT;
	protected static int BORDER_OFFSET = 5;
	private ArrayList<Wall> walls;
	
	public Level() {
		walls = new ArrayList<Wall>();
	}
	
	private void initializeBorders() {
		// Left wall
		addWall(-BORDER_OFFSET, -BORDER_OFFSET, X_BORDER_SIZE, SCREEN_HEIGHT + BORDER_OFFSET);
		// Right wall
		addWall(SCREEN_WIDTH - X_BORDER_SIZE, -BORDER_OFFSET, SCREEN_WIDTH+ BORDER_OFFSET, SCREEN_HEIGHT + BORDER_OFFSET);
		// Top wall
		addWall(-BORDER_OFFSET, -BORDER_OFFSET, SCREEN_WIDTH + BORDER_OFFSET, Y_BORDER_SIZE);
		// Bottom wall
		addWall(-BORDER_OFFSET, SCREEN_HEIGHT - Y_BORDER_SIZE, SCREEN_WIDTH + BORDER_OFFSET, SCREEN_HEIGHT + BORDER_OFFSET);
	}
	
	protected void addWall(int x1, int y1, int x2, int y2) {
		walls.add(new Wall(x1, y1 + GameCanvas.HUD_HEIGHT, x2, y2 + GameCanvas.HUD_HEIGHT));
	}
	
	protected void addWallByWidth(int x, int y, int width, int height) {
		walls.add(new Wall(x, y, x + width, y + height));
	}

	protected void addMirroredWall(int x1, int y1, int x2, int y2) {
		addWall(x1, y1, x2, y2);
		addWall(SCREEN_WIDTH - x1, SCREEN_HEIGHT - y2, SCREEN_WIDTH - x2, SCREEN_HEIGHT - y2);
	}
	
	protected void addMirroredWallByWidth(int x, int y, int width, int height) {
		addWallByWidth(x, y, width, height);
		addWallByWidth(SCREEN_WIDTH - x, y, -width, height);
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Wall> getWalls() {
		initializeBorders();
		return walls;
	}
}
