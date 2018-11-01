package main;

import java.awt.Point;

public class MathStuffs {
	public static double AngleBetween(Point origin, double currentAngle, Point mouse) {
		double newAngle = Math.atan2(mouse.y - origin.y, mouse.x - origin.x);
		
		double angle = currentAngle - newAngle;
		if(angle > Math.PI) {
			angle -= Math.PI;
		}
		else if(angle < -Math.PI) {
			angle += Math.PI;
		}
		
		return angle;
	}
}
