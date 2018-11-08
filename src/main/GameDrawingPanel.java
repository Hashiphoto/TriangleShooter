package main;

import java.awt.Color;
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
	
	public GameDrawingPanel(ArrayList<Ship> ships, ArrayList<Bullet> bullets) {
		this.ships = ships;
		this.bullets = bullets;
		this.setBackground(Color.BLACK);
		numShips = ships.size();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawShips(g);
		drawBullets(g);
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
			g.setColor(Constants.ShipColors[s.getId()]);
			int x = s.getLocation().x;
			int y = s.getLocation().y;
			g.drawOval(x - Constants.SHIP_OVAL_SIZE / 2, y - Constants.SHIP_OVAL_SIZE / 2, Constants.SHIP_OVAL_SIZE, Constants.SHIP_OVAL_SIZE);
//			g.drawLine(x, y, x + (int)(150 * Math.cos(s.getRotation())), y + (int)(150 * Math.sin(s.getRotation())));
			
			int[][] shipVertices = MathStuffs.shipVertices(s.getLocation(), s.getRotation());

	        g.drawPolygon(shipVertices[0], shipVertices[1], shipVertices[0].length);
		}
	}
	
	private void drawBullets(Graphics g) {
		int numBullets = bullets.size();
		for (int i = 0; i < numBullets; i++) {
			Bullet b = bullets.get(i);
			
			g.drawOval((int) b.getX() - 2, (int) b.getY() - 2, 4, 4);
		}
	}
}
