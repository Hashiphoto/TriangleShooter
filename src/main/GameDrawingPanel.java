package main;

import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GameDrawingPanel extends JPanel {
	
	private ArrayList<Ship> ships;
	private ArrayList<Bullet> bullets;
	private int numShips;
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawShips(g);
		drawBullets(g);
	}
	
	public GameDrawingPanel(ArrayList<Ship> ships, ArrayList<Bullet> bullets) {
		this.ships = ships;
		this.bullets = bullets;
		numShips = ships.size();
	}
	
	public Point getMouseLocation() {
		Point mousePosition = new Point();
		mousePosition.x = MouseInfo.getPointerInfo().getLocation().x - this.getLocationOnScreen().x;
		mousePosition.y = MouseInfo.getPointerInfo().getLocation().y - this.getLocationOnScreen().y;
		return mousePosition;
	}
	
	private void drawShips(Graphics g) {
		for (int i = 0; i < numShips; i++) {
			Ship s = ships.get(i);
			
			g.drawOval(s.getX() - 5, s.getY() - 5, 10, 10);
			g.drawLine(s.getX(), s.getY(), s.getX() + (int)(150 * Math.cos(s.getRotation())), s.getY() + (int)(150 * Math.sin(s.getRotation())));
		}
	}
	
	private void drawBullets(Graphics g) {
		int numBullets = bullets.size();
		for (int i = 0; i < numBullets; i++) {
			Bullet b = bullets.get(i);
			
			g.drawOval(b.getX() - 2, b.getY() - 2, 4, 4);
		}
	}
}
