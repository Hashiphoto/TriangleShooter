package main;

import java.util.ArrayList;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameCanvas extends Canvas {
	
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
			gc.setStroke(Constants.ShipColors[s.getId()]);
			int x = s.getLocation().x;
			int y = s.getLocation().y;
			gc.strokeOval(x - Constants.SHIP_OVAL_SIZE / 2, y - Constants.SHIP_OVAL_SIZE / 2, Constants.SHIP_OVAL_SIZE, Constants.SHIP_OVAL_SIZE);
			
			double[][] shipVertices = MathStuffs.shipVertices(s.getLocation(), s.getRotation());

	        gc.strokePolyline(shipVertices[0], shipVertices[1], shipVertices[0].length);
		}
	}
	
	private void drawBullets() {
		int numBullets = bullets.size();
		for (int i = 0; i < numBullets; i++) {
			Bullet b = bullets.get(i);
			gc.setFill(Constants.ShipColors[b.getId()]);
			gc.fillOval((int) b.getX() - 2, (int) b.getY() - 2, 4, 4);
		}
	}
}
