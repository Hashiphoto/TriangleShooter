package main;

import java.awt.Point;

public class Bullet {
	private Point location;
	private double rotation;
	private int id;
	private int speed;
	private int range;
	private double accuracy;
	
	public Bullet(int id, Point location, double rotation, int speed, int range, double accuracy) {
		this.id = id;
		this.location = location;
		this.rotation = rotation;
		this.speed = speed;
		this.range = range;
		this.accuracy = accuracy;
	}
	
	public void step() {
//		System.out.println(rotation);
		location.x += Math.cos(rotation) * speed;
		location.y += Math.sin(rotation) * speed;
	}
	
	public int getX() {
		return location.x;
	}
	
	public int getY() {
		return location.y;
	}
}
