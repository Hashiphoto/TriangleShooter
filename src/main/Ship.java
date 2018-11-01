package main;

import java.awt.Point;

public class Ship {
	private int id;
	private Point location;
	private double rotation;
	private double rotationSpeed;
	private int xSpeed;
	private int ySpeed;
	private int shipMaxSpeed;
	private double shipAcceleration;

	private boolean[] upgrades;
	
	private int bulletSpeed;
	private int bulletDistance;
	private int reloadTime;
	private int reloadProgress;
	
	public Ship(int id, Point start) {
		this.id = id;
		this.location = start;
		this.rotation = 0;
		this.xSpeed = 0;
		this.ySpeed = 0;
		this.reloadProgress = 0;
		this.rotationSpeed = Constants.DEFAULT_SHIP_ROTATION_SPEED;
		this.shipMaxSpeed = Constants.DEFAULT_SHIP_MAX_SPEED;
		this.shipAcceleration = Constants.DEFAULT_SHIP_ACCEL;
		this.upgrades = new boolean[Constants.GetNumUpgrades()];
		this.bulletSpeed = Constants.DEFAULT_BULLET_SPEED;
		this.reloadTime = Constants.DEFAULT_RELOAD_TIME;
	}
	
	public void move(int dir) {
		switch(dir) {
		case Constants.UP:
			ySpeed -= shipAcceleration;
			if(ySpeed < -shipMaxSpeed) {
				ySpeed = -shipMaxSpeed;
			}
			location.y -= ySpeed;
			break;
		case Constants.DOWN:
			ySpeed += shipAcceleration;
			if(ySpeed > shipMaxSpeed) {
				ySpeed = shipMaxSpeed;
			}
			location.y += ySpeed;
			break;
		case Constants.LEFT:
			xSpeed -= shipAcceleration;
			if(xSpeed < -shipMaxSpeed) {
				xSpeed = -shipMaxSpeed;
			}
			location.x -= xSpeed;
			break;
		case Constants.RIGHT:
			xSpeed += shipAcceleration;
			if(xSpeed > shipMaxSpeed) {
				xSpeed = shipMaxSpeed;
			}
			location.x += xSpeed;
			break;
		default:
			System.err.println("Error moving ship " + id + ". Not a valid direction");
		}
	}
	
	public void setDirection(Point mouse) {
		double angleDifference = MathStuffs.AngleBetween(location, rotation, mouse);
		if (angleDifference < rotationSpeed) {
			rotation += angleDifference;
			return;
		}
		if (angleDifference > 0) {
			rotation += rotationSpeed;
		} else {
			rotation -= rotationSpeed;
		}
	}
}
