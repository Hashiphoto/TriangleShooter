package levels;

import java.util.ArrayList;

import gameElements.Wall;
import gui.GameCanvas;

public class Level {
	private static final int BORDER_SIZE = 30;
	private static final int SCREEN_WIDTH = 1280;
	private static final int SCREEN_HEIGHT = 720 - GameCanvas.HUD_HEIGHT;
	private static final int BORDER_OFFSET = 5;
	private ArrayList<Wall> walls;
	
	public Level() {
		walls = new ArrayList<Wall>();
		
		// Left wall
		addWall(-BORDER_OFFSET, -BORDER_OFFSET, BORDER_SIZE, SCREEN_HEIGHT + BORDER_OFFSET);
		// Right wall
		addWall(SCREEN_WIDTH - BORDER_SIZE, -BORDER_OFFSET, SCREEN_WIDTH+ BORDER_OFFSET, SCREEN_HEIGHT + BORDER_OFFSET);
		// Top wall
		addWall(-BORDER_OFFSET, -BORDER_OFFSET, SCREEN_WIDTH + BORDER_OFFSET, BORDER_SIZE);
		// Bottom wall
		addWall(-BORDER_OFFSET, SCREEN_HEIGHT - BORDER_SIZE, SCREEN_WIDTH + BORDER_OFFSET, SCREEN_HEIGHT + BORDER_OFFSET);
	}
	
	private void addWall(int x1, int y1, int x2, int y2) {
		walls.add(new Wall(x1, y1 + GameCanvas.HUD_HEIGHT, x2, y2 + GameCanvas.HUD_HEIGHT));
	}
	
	private void addMirroredWall(int x1, int y1, int x2, int y2) {
		addWall(x1, y1, x2, y2);
		addWall(SCREEN_WIDTH - x1, SCREEN_HEIGHT - y2, SCREEN_WIDTH - x2, SCREEN_HEIGHT - y2);
	}
	
	public ArrayList<Wall> getWalls() {
		return walls;
	}
}
