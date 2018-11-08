package main;

import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GameDrawingPanel extends JPanel {
	
	private ArrayList<Ship> ships;
	private int numShips;
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawShips(g);
	}
	
	public GameDrawingPanel(ArrayList<Ship> ships) {
		this.ships = ships;
		numShips = ships.size();
	}
	
	public Point getMouseLocation() {
		Point mousePosition = new Point();
		mousePosition.x = MouseInfo.getPointerInfo().getLocation().x - this.getLocationOnScreen().x;
		mousePosition.y = MouseInfo.getPointerInfo().getLocation().y - this.getLocationOnScreen().y;
		return mousePosition;
	}
	
	private void drawShips(Graphics g) {
		for(int i = 0; i < numShips; i++) {
			Ship s = ships.get(i);
			
			g.drawOval(s.getX(), s.getY(), 5, 5);
			g.drawLine(s.getX(), s.getY(), s.getX() + (int)(150 * Math.cos(s.getRotation())), s.getY() + (int)(150 * Math.sin(s.getRotation())));
		}
	}
}
