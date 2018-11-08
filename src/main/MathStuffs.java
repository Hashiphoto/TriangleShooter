package main;

import java.awt.Point;

public class MathStuffs {
	public static double calculateNewAngle(Point origin, Point mouse, double currentAngle, double turnSpeed) {
		Point mouseUnitVector = new Point();
		Point curDirUnitVector = new Point();
		Point newUnitVector = new Point();
		double distanceToMouse = Math.sqrt(Math.pow(mouse.x - origin.x, 2) + Math.pow(mouse.y - origin.y, 2));

		mouseUnitVector.x = (int) (((mouse.x - origin.x) / distanceToMouse) * Constants.TURN_PRECISION);
		mouseUnitVector.y = (int) (((mouse.y - origin.y) / distanceToMouse) * Constants.TURN_PRECISION);

		curDirUnitVector.x = (int) ((Math.cos(currentAngle)) * Constants.TURN_PRECISION);
		curDirUnitVector.y = (int) ((Math.sin(currentAngle)) * Constants.TURN_PRECISION);

		newUnitVector.x = (int) ((int) (mouseUnitVector.x + turnSpeed * curDirUnitVector.x) / 1.5);
		newUnitVector.y = (int) ((int) (mouseUnitVector.y + turnSpeed * curDirUnitVector.y) / 1.5);
		
		return Math.atan2(newUnitVector.y , newUnitVector.x);
	}
	
	public static double distance(Point a, Point b) {
		return Math.sqrt(Math.pow((a.y - b.y), 2) + Math.pow((a.x - b.x), 2));
	}
	
	public static Point bulletMove(Point bullet, Point target, int speed) {
		// Get the distance between the bullet and target
		double dist = distance(bullet, target);
		
		// Calculate how many parts the total length can be divided into 
		double ratio = speed / dist;
		int newX = (int) ((bullet.x + target.x) / 2);
		int newY = (int) ((bullet.y + target.y) / 2);
		
		return new Point(newX, newY);
	}
	
	public static int[][] shipVertices(Point location, double rotation) {
		int x[] = {
        		location.x + (int) (Math.cos(rotation) * Constants.SHIP_FRONT_LENGTH),
        		location.x + (int) (Math.cos(rotation+Math.PI*2/3) * Constants.SHIP_SIDE_LENGTH),
        		location.x + (int) (Math.cos(rotation+Math.PI*4/3) * Constants.SHIP_SIDE_LENGTH)
        };
        int y[] = {
        		location.y + (int) (Math.sin(rotation) * Constants.SHIP_FRONT_LENGTH),
        		location.y + (int) (Math.sin(rotation+Math.PI*2/3) * Constants.SHIP_SIDE_LENGTH),
        		location.y + (int) (Math.sin(rotation+Math.PI*4/3) * Constants.SHIP_SIDE_LENGTH)
        };
        
        return new int[][] {x, y};
	}
}
