package gameControl;

import java.awt.Point;
import java.util.ArrayList;

import gameElements.Bullet;
import gameElements.Ship;
import gameElements.Wall;

public class MathStuffs {
	private static final int TURN_PRECISION = 1000;
	private static final int COLLISION_DISTANCE = 35;
	private static final int WALL_COLLISION_DISTANCE = 15;
	
	public static Point collide(Point position, ArrayList<Wall> walls) {
		int numWalls = walls.size();
		Point newPosition = new Point(position.x, position.y);
		boolean gotX = false;
		boolean gotY = false;
		for(int i = 0; i < numWalls; i++) {
			Wall wall = walls.get(i);
			if(!gotX) {
				boolean linedUpHor = (position.x < wall.x1 || position.x > wall.x2) && position.y - WALL_COLLISION_DISTANCE < wall.y2 && position.y + WALL_COLLISION_DISTANCE > wall.y1;
				if(linedUpHor) {
					// Hitting the right wall
					if(position.x > wall.centerX() && position.x - WALL_COLLISION_DISTANCE < wall.x2) {
						newPosition.x = wall.x2 + WALL_COLLISION_DISTANCE;
						gotX = true;
						continue;
					} 
					// Hitting the left wall
					if(position.x < wall.centerX() && position.x + WALL_COLLISION_DISTANCE > wall.x1) {
						newPosition.x = wall.x1 - WALL_COLLISION_DISTANCE;
						gotX = true;
						continue;
					}
				}
			}
			if(!gotY) {
				boolean linedUpVer = (position.y < wall.y1 || position.y > wall.y2) && position.x - WALL_COLLISION_DISTANCE < wall.x2 && position.x + WALL_COLLISION_DISTANCE > wall.x1;
				if(linedUpVer) {
					// Hitting the bottom wall
					if(position.y > wall.centerY() && position.y - WALL_COLLISION_DISTANCE < wall.y2) {
						newPosition.y = wall.y2 + WALL_COLLISION_DISTANCE;
						gotY = true;
						continue;
					} 
					// Hitting the top wall
					if(position.y < wall.centerY() && position.y + WALL_COLLISION_DISTANCE > wall.y1) {
						newPosition.y = wall.y1 - WALL_COLLISION_DISTANCE;
						gotY = true;
						continue;
					}
				}
			}
		}
		return newPosition;
	}
	
	public static double calculateNewAngle(Point origin, Point mouse, double currentAngle, double turnSpeed) {
		Point mouseUnitVector = new Point();
		Point curDirUnitVector = new Point();
		Point newUnitVector = new Point();
		double distanceToMouse = Math.sqrt(Math.pow(mouse.x - origin.x, 2) + Math.pow(mouse.y - origin.y, 2));

		mouseUnitVector.x = (int) (((mouse.x - origin.x) / distanceToMouse) * TURN_PRECISION);
		mouseUnitVector.y = (int) (((mouse.y - origin.y) / distanceToMouse) * TURN_PRECISION);

		curDirUnitVector.x = (int) ((Math.cos(currentAngle)) * TURN_PRECISION);
		curDirUnitVector.y = (int) ((Math.sin(currentAngle)) * TURN_PRECISION);

		newUnitVector.x = (int) ((int) (mouseUnitVector.x + turnSpeed * curDirUnitVector.x) / 1.5);
		newUnitVector.y = (int) ((int) (mouseUnitVector.y + turnSpeed * curDirUnitVector.y) / 1.5);
		
		return Math.atan2(newUnitVector.y , newUnitVector.x);
	}
	
	public static double distance(Point a, Point b) {
		return Math.sqrt(Math.pow((a.y - b.y), 2) + Math.pow((a.x - b.x), 2));
	}
	
	public static Point bulletMove(Point bullet, Point target, int speed) {
		int newX = (int) ((bullet.x + target.x) / 2);
		int newY = (int) ((bullet.y + target.y) / 2);
		
		return new Point(newX, newY);
	}
	
	public static double[][] shipVertices(Point location, double rotation, int sideLength, int frontLength) {
		double x[] = {
        		location.x + (int) (Math.cos(rotation) * frontLength),
        		location.x + (int) (Math.cos(rotation+Math.PI*2/3) * sideLength),
        		location.x + (int) (Math.cos(rotation+Math.PI*4/3) * sideLength),
        		location.x + (int) (Math.cos(rotation) * frontLength)
        };
        double y[] = {
        		location.y + (int) (Math.sin(rotation) * frontLength),
        		location.y + (int) (Math.sin(rotation+Math.PI*2/3) * sideLength),
        		location.y + (int) (Math.sin(rotation+Math.PI*4/3) * sideLength),
        		location.y + (int) (Math.sin(rotation) * frontLength)
        };
        
        return new double[][] {x, y};
	}
	
	public static boolean isCollision(Bullet b, Ship s) {
		double distance = distance(new Point(b.getX(), b.getY()), new Point(s.getLocation()));
		return distance < COLLISION_DISTANCE;
	}
}
