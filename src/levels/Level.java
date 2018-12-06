package levels;

import java.util.ArrayList;

import gameElements.Wall;
import gui.GameCanvas;

/**
 * This class defines the walls in an empty level, and is designed for other levels to be created as
 * extensions of this class
 * @author Trent
 *
 */
public class Level {
	protected int X_BORDER_SIZE = 30;
	protected int Y_BORDER_SIZE = 30;
	protected int SCREEN_WIDTH = 1280;
	protected int SCREEN_HEIGHT = 720 - GameCanvas.HUD_HEIGHT;
	protected int BORDER_OFFSET = 5;
	private ArrayList<Wall> walls;
	
	/**
	 * Instantiate a new Level
	 */
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
	
	/**
	 * Create a wall based on two sets of x and y coordinates
	 * @param x1	The first point's x coordinate
	 * @param y1	The first point's y coordinate	
	 * @param x2	The second point's x coordinate
	 * @param y2	The second point's y coordinate
	 */
	protected void addWall(int x1, int y1, int x2, int y2) {
		walls.add(new Wall(x1, y1 + GameCanvas.HUD_HEIGHT, x2, y2 + GameCanvas.HUD_HEIGHT));
	}
	
	/**
	 * Create a wall based on the x and y coordinate of the top left corner and a given width and height
	 * @param x			The top left corner's x coordinate
	 * @param y			The top left corner's y coordinate
	 * @param width		The width of the wall in pixels
	 * @param height	The height of the wall in pixels
	 */
	protected void addWallByWidth(int x, int y, int width, int height) {
		walls.add(new Wall(x, y, x + width, y + height));
	}

	/**
	 * This creates a wall the same as addWall() but also creates a second wall mirrored horizontally across the y axis
	 * @param x1	The first point's x coordinate
	 * @param y1	The first point's y coordinate	
	 * @param x2	The second point's x coordinate
	 * @param y2	The second point's y coordinate
	 */
	protected void addMirroredWall(int x1, int y1, int x2, int y2) {
		addWall(x1, y1, x2, y2);
		addWall(SCREEN_WIDTH - x1, SCREEN_HEIGHT - y2, SCREEN_WIDTH - x2, SCREEN_HEIGHT - y2);
	}

	/**
	 * This creates a wall the same as addWallByWidth() but also creates a second wall mirrored horizontally across the y axis
	 * @param x			The top left corner's x coordinate
	 * @param y			The top left corner's y coordinate
	 * @param width		The width of the wall in pixels
	 * @param height	The height of the wall in pixels
	 */
	protected void addMirroredWallByWidth(int x, int y, int width, int height) {
		addWallByWidth(x, y, width, height);
		addWallByWidth(SCREEN_WIDTH - x, y, -width, height);
	}
	
	/**
	 * Use this function to get all the walls in a given level. The surrounding borders are initialized here and not in
	 * the constructor so that inherited classes can modify the boundaries
	 * @return	An ArrayList of all the walls in this level
	 */
	public ArrayList<Wall> getWalls() {
		initializeBorders();
		return walls;
	}
}
