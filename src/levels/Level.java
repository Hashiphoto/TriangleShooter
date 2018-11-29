package levels;

import java.util.ArrayList;

import gameElements.Wall;
import gui.GameCanvas;

public class Level {
	private static final int BORDER_SIZE = 30;
	private static final int SCREEN_WIDTH = 1280;
	private static final int SCREEN_HEIGHT = 720 - GameCanvas.HUD_HEIGHT;
	private ArrayList<Wall> walls;
	
	public Level() {
		walls = new ArrayList<Wall>();
		
		// Left wall
		addWall(0, 0, BORDER_SIZE, SCREEN_HEIGHT);
		// Right wall
		addWall(SCREEN_WIDTH - BORDER_SIZE, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		// Top wall
		addWall(0, 0, SCREEN_WIDTH, BORDER_SIZE);
		// Bottom wall
		addWall(0, SCREEN_HEIGHT - BORDER_SIZE, SCREEN_WIDTH, SCREEN_HEIGHT);
	}
	
	private void addWall(int x1, int y1, int x2, int y2) {
		walls.add(new Wall(x1, y1 + GameCanvas.HUD_HEIGHT, x2, y2 + GameCanvas.HUD_HEIGHT));
	}
	
	public ArrayList<Wall> getWalls() {
		return walls;
	}
}
