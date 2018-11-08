package main;

import java.awt.Point;

public class MathStuffs {
	public static double CalculateNewAngle(Point origin, Point mouse, double currentAngle, double turnSpeed) {
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
}
