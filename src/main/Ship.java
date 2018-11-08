package main;

import java.awt.Point;

import com.sun.glass.events.KeyEvent;

public class Ship {
	private int id;
	private Point location;
	private double rotation;
	private double rotationSpeed;
	private double xSpeed;
	private double ySpeed;
	private int shipMaxSpeed;
	private double shipAcceleration;
	private boolean[] keysHeld;
	
	private boolean[] upgrades;
	
	private int bulletSpeed;
	private int bulletRange;
	private double accuracy;
	private int reloadTime;
	private int reloadProgress;
	
	public Ship(int id, Point start) {
		this.id = id;
		this.location = start;
		this.rotation = 0;
		this.xSpeed = 0;
		this.ySpeed = 0;
		this.reloadProgress = 0;
		this.keysHeld = new boolean[4];
		this.accuracy = Constants.DEFAULT_ACCURACY;
		this.rotationSpeed = Constants.DEFAULT_SHIP_ROTATION_SPEED;
		this.shipMaxSpeed = Constants.DEFAULT_SHIP_MAX_SPEED;
		this.shipAcceleration = Constants.DEFAULT_SHIP_ACCEL;
		this.upgrades = new boolean[Constants.GetNumUpgrades()];
		this.bulletSpeed = Constants.DEFAULT_BULLET_SPEED;
		this.bulletRange = Constants.DEFAULT_BULLET_RANGE;
		this.reloadTime = Constants.DEFAULT_RELOAD_TIME;
	}
	
	// Movement
	public void keyPressed(int keyCode) {
		switch(keyCode) {
		case KeyEvent.VK_W:
			keysHeld[Constants.UP] = true;
			break;
		case KeyEvent.VK_S:
			keysHeld[Constants.DOWN] = true;
			break;
		case KeyEvent.VK_A:
			keysHeld[Constants.LEFT] = true;
			break;
		case KeyEvent.VK_D:
			keysHeld[Constants.RIGHT] = true;
			break;
		}
	}
	
	public void keyReleased(int keyCode) {
		switch(keyCode) {
		case KeyEvent.VK_W:
			keysHeld[Constants.UP] = false;
			break;
		case KeyEvent.VK_S:
			keysHeld[Constants.DOWN] = false;
			break;
		case KeyEvent.VK_A:
			keysHeld[Constants.LEFT] = false;
			break;
		case KeyEvent.VK_D:
			keysHeld[Constants.RIGHT] = false;
			break;
		}
	}
	
	public void move() {
		if (keysHeld[Constants.UP]) {
			ySpeed -= shipAcceleration;
			if (ySpeed < -shipMaxSpeed) {
				ySpeed = -shipMaxSpeed;
			}
		}
		else if (keysHeld[Constants.DOWN]) {
			ySpeed += shipAcceleration;
			if (ySpeed > shipMaxSpeed) {
				ySpeed = shipMaxSpeed;
			}
		}
		else {
			if (ySpeed > 0) {
				ySpeed -= shipAcceleration;
				if(ySpeed < 0) {
					ySpeed = 0;
				}
			} 
			else if (ySpeed < 0) {
				ySpeed += shipAcceleration;
				if(ySpeed > 0) {
					ySpeed = 0;
				}
			}
		}
		if (keysHeld[Constants.LEFT]) {
			xSpeed -= shipAcceleration;
			if (xSpeed < -shipMaxSpeed) {
				xSpeed = -shipMaxSpeed;
			}
		}
		else if (keysHeld[Constants.RIGHT]) {
			xSpeed += shipAcceleration;
			if (xSpeed > shipMaxSpeed) {
				xSpeed = shipMaxSpeed;
			}
		}
		else {
			if (xSpeed > 0) {
				xSpeed -= shipAcceleration;
				if(xSpeed < 0) {
					xSpeed = 0;
				}
			} 
			else if (xSpeed < 0) {
				xSpeed += shipAcceleration;
				if(xSpeed > 0) {
					xSpeed = 0;
				}
			}
		}

		location.x += xSpeed;
		location.y += ySpeed;
//		System.out.println(location.x + "," + location.y);
//		System.out.println(xSpeed + "," + ySpeed);
	}
	
	public void setDirection(Point mouse) {
		rotation = MathStuffs.calculateNewAngle(location, mouse, rotation, rotationSpeed);
	}
	
	// Shooting
	public Bullet createBullet() {
		System.out.println("Ship: " + rotation);
		Bullet newBullet = new Bullet(id, new Point(location), new Double(rotation), bulletSpeed, bulletRange, accuracy);
		
		return newBullet;
	}
	
	public int getId() {
		return id;
	}
	
	public Point getLocation() {
		return location;
	}
	
	public double getRotation() {
		return rotation;
	}
}
