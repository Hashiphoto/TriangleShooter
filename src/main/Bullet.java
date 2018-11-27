package main;

import java.awt.Point;

public class Bullet {
	private Point initialLocation;
	private double x;
	private double y;
	private double rotation;
	private int id;
	private int speed;
	private int range;
//	private double accuracy;
	
	public Bullet(int id, Point location, double rotation, int speed, int range, double accuracy) {
		this.id = id;
		this.initialLocation = location;
		this.x = location.x;
		this.y = location.y;
		this.rotation = rotation;
		this.speed = speed;
		this.range = range;
//		this.accuracy = accuracy;
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
	
	public int getId() {
		return id;
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
}
