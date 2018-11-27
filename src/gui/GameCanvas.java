package gui;

import java.util.ArrayList;

import gameControl.MathStuffs;
import gameElements.Bullet;
import gameElements.Ship;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameCanvas extends Canvas {
	private static final Color[] ShipColors = {
		Color.CYAN,
		Color.MAGENTA
	};
	private static final int SHIP_SIDE_LENGTH = 22;
	private static final int SHIP_FRONT_LENGTH = 40;
	private static final int SHIP_OVAL_SIZE = 15;
	private static final int BULLET_SIZE = 6;
	private ArrayList<Ship> ships;
	private ArrayList<Bullet> bullets;
	private int numShips;
	GraphicsContext gc;
	
	public GameCanvas(int width, int height) {
		super(width, height);
		gc = this.getGraphicsContext2D();
	}

	public void init(ArrayList<Ship> ships, ArrayList<Bullet> bullets) {
		this.ships = ships;
		this.bullets = bullets;
		numShips = ships.size();
	}
	public void repaint() {
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, this.getWidth(), this.getHeight());
		drawShips();
		drawBullets();
	}
	
	private void drawShips() {
		for (int i = 0; i < numShips; i++) {
			Ship s = ships.get(i);
			gc.setStroke(ShipColors[s.getId()]);
			int x = s.getLocation().x;
			int y = s.getLocation().y;
			gc.strokeOval(x - SHIP_OVAL_SIZE / 2, y - SHIP_OVAL_SIZE / 2, SHIP_OVAL_SIZE, SHIP_OVAL_SIZE);
			
			double[][] shipVertices = MathStuffs.shipVertices(s.getLocation(), s.getRotation(), SHIP_SIDE_LENGTH, SHIP_FRONT_LENGTH);

	        gc.strokePolyline(shipVertices[0], shipVertices[1], shipVertices[0].length);
		}
	}
	
	private void drawBullets() {
		int numBullets = bullets.size();
		for (int i = 0; i < numBullets; i++) {
			Bullet b = bullets.get(i);
			gc.setFill(ShipColors[b.getId()]);
			gc.fillOval((int) b.getX() - (BULLET_SIZE / 2), (int) b.getY() - (BULLET_SIZE / 2), BULLET_SIZE, BULLET_SIZE);
		}
	}
}
