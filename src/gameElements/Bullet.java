package gameElements;

import java.awt.Point;

import gameControl.MathStuffs;


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
	
	public Bullet(int player, Point location, double rotation, int speed, int range, double accuracy, int damage, int size) {
		this(player, location, rotation, speed, range, accuracy, damage, size, BulletCounter.getNextId());
	}
	
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
	
	// Returns false if the bullet has expired
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
	
	public void setOffset(double offset) {
		rotation += offset;
	}
}
