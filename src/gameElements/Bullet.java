package gameElements;

import java.awt.Point;

import gameControl.MathStuffs;

/**
 * A class representing a bullet object. Bullets themselves are not sent over the network, but 
 * @author Trent
 *
 */
public class Bullet {
	private Point initialLocation;
	private double x;
	private double y;
	private double rotation;
	private int player;
	private int speed;
	private int range;
	private int damage;
//	private double accuracy;
	private int id;
	private int radius;
	private int size;
	
	/**
	 * Instantiate a friendly bullet
	 * @param player	The player that created the Bullet. Used to determine collisions 
	 * @param location	The starting point of the bullet
	 * @param rotation	The angle the bullet travels on
	 * @param speed		The speed of the bullet in pixels/frame
	 * @param range		The maximum distance the bullet can travel from the origin in a straight line
	 * @param accuracy	This is unused
	 * @param damage	How much health will be subtracted from the opponent on impact
	 * @param size		The circumference of the Bullet
	 */
	public Bullet(int player, Point location, double rotation, int speed, int range, double accuracy, int damage, int size) {
		this(player, location, rotation, speed, range, accuracy, damage, size, BulletCounter.getNextId());
	}
	
	/**
	 * Instantiate an enemy bullet
	 * @param player	The player that created the Bullet. Used to determine collisions 
	 * @param location	The starting point of the bullet
	 * @param rotation	The angle the bullet travels on
	 * @param speed		The speed of the bullet in pixels/frame
	 * @param range		The maximum distance the bullet can travel from the origin in a straight line
	 * @param accuracy	This is unused
	 * @param damage	How much health will be subtracted from the opponent on impact
	 * @param size		The circumference of the Bullet
	 * @param id		The identifying number of the bullet. This is used to destroy bullets that have come in contact with the opponent
	 */
	public Bullet(int player, Point location, double rotation, int speed, int range, double accuracy, int damage, int size, int id) {
		this.player = player;
		this.initialLocation = location;
		this.x = location.x;
		this.y = location.y;
		this.rotation = rotation;
		this.speed = speed;
		this.range = range;
		this.damage = damage;
//		this.accuracy = accuracy;
		this.size = size;
		this.radius = size / 2;
		this.id = id;
	}
	
	/**
	 * Move the bullet according to its angle and direction
	 * @return	True if the bullet is still alive
	 */
	public boolean step() {
		if (MathStuffs.distance(initialLocation, new Point((int)x, (int)y)) > range) {
			return false;
		}
		x += Math.cos(rotation) * speed;
		y += Math.sin(rotation) * speed;
		
		return true;
	}
	
	public int getPlayer() {
		return player;
	}
	
	public int getX() {
		return (int) x;
	}
	
	public int getY() {
		return (int) y;
	}
	
	public double getRotation() {
		return rotation;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public int getRange() {
		return range;
	}
	
	public int getDamage() {
		return damage;
	}
	
	public int getId() {
		return id;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getRadius() {
		return radius;
	}
	
	/**
	 * This is used to replicate the random offset determined by the firing ship
	 * @param offset	This is a added to the current angle and can be positive or negative
	 */
	public void setOffset(double offset) {
		rotation += offset;
	}
}
