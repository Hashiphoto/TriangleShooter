package main;

import java.awt.Point;

public class Bullet {
	private double x;
	private double y;
	private double rotation;
	private int id;
	private int speed;
	private int range;
	private double accuracy;
	
	public Bullet(int id, Point location, double rotation, int speed, int range, double accuracy) {
		this.id = id;
		this.x = location.x;
		this.y = location.y;
		this.rotation = rotation;
		this.speed = speed;
		this.range = range;
		this.accuracy = accuracy;
	}
	
	public boolean step() {
		x += Math.cos(rotation) * speed;
		y += Math.sin(rotation) * speed;
		
		return true;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
}
