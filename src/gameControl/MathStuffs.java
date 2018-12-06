package gameControl;

import java.awt.Point;
import java.util.ArrayList;

import gameElements.Bullet;
import gameElements.Ship;
import gameElements.Wall;

/**
 * This class is responsible for doing lengthy math calculations. All methods
 * contained within are static and designed to be called for single calcuations
 * at a time 
 * @author Trent
 */
public abstract class MathStuffs {
	private static final int TURN_PRECISION = 1000;
	private static final int WALL_COLLISION_DISTANCE = 15;
	
	/**
	 * @param position	The position of the Ship object
	 * @param walls		The wall we are checking if the ship has hit
	 * @return			The new location that the ship should be at
	 */
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
	
	/**
	 * This is used to turn the ship a certain amount based on the mouse location and turning speed
	 * of the ship.
	 * @param origin		The position of the ship
	 * @param mouse			The position of the mouse
	 * @param currentAngle	The angle the ship is currently facing
	 * @param turnSpeed		The speed at which the ship is allowed to turn. 
	 * @return				The new angle of the ship, in radians
	 */
	public static double turnShipToAngle(Point origin, Point mouse, double currentAngle, double turnSpeed) {
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
	
	/**
	 * The distance between two points
	 * @param point1	The first point
	 * @param point2	The second point
	 * @return	The diagonal distance between a and b
	 */
	public static double distance(Point point1, Point point2) {
		return Math.sqrt(Math.pow((point1.y - point2.y), 2) + Math.pow((point1.x - point2.x), 2));
	}
	
	/**
	 * Calculates the next location for a bullet to move to based on its speed
	 * @param bullet	The current position of the bullet
	 * @param target	The point the bullet is moving towards
	 * @param speed		The velocity at which the bullet is traveling, in pixels per cycle
	 * @return			The new location of the bullet
	 */
	public static Point bulletMove(Point bullet, Point target, int speed) {
		int newX = (int) ((bullet.x + target.x) / 2);
		int newY = (int) ((bullet.y + target.y) / 2);
		
		return new Point(newX, newY);
	}
	
	/**
	 * Get the three points that make up the ship's triangle 
	 * @param location		The position of the ship
	 * @param rotation		The current angle of the ship
	 * @param sideLength	The distance of the two points behind the ship from the ship's origin 
	 * @param frontLength	The distance to the tip of the ship from the ship's origin
	 * @return				A 2D array of the ship's x and y coordinates
	 */
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
	
	/**
	 * Check if a ship has been hit by a given bullet
	 * @param bullet	The bullet
	 * @param ship		The ship
	 * @return			Whether the ship's radius crosses the bullet's radius
	 */
	public static boolean isCollision(Bullet bullet, Ship ship) {
		double distance = distance(new Point(bullet.getX(), bullet.getY()), new Point(ship.getLocation()));
		return distance < bullet.getRadius() + ship.getRadius();
	}
}
