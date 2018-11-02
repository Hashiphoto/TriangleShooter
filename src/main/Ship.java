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
	private int bulletDistance;
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
		this.reloadTime = Constants.DEFAULT_RELOAD_TIME;
	}
	
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
			location.y += ySpeed;
		}
		else if (keysHeld[Constants.DOWN]) {
			ySpeed += shipAcceleration;
			if (ySpeed > shipMaxSpeed) {
				ySpeed = shipMaxSpeed;
			}
			location.y += ySpeed;
		}
		if (keysHeld[Constants.LEFT]) {
			xSpeed -= shipAcceleration;
			if (xSpeed < -shipMaxSpeed) {
				xSpeed = -shipMaxSpeed;
			}
			location.x += xSpeed;
		}
		else if (keysHeld[Constants.RIGHT]) {
			xSpeed += shipAcceleration;
			if (xSpeed > shipMaxSpeed) {
				xSpeed = shipMaxSpeed;
			}
			location.x += xSpeed;
		}
		if (noKeysHeld()) {
			if (xSpeed != 0) {
				if (xSpeed > 0) {
					xSpeed -= shipAcceleration;
					if (xSpeed < 0) {
						xSpeed = 0;
					}
				} else {
					xSpeed += shipAcceleration;
					if (xSpeed > 0) {
						xSpeed = 0;
					}
				}
				location.x += xSpeed;
			} else {
				if (ySpeed > 0) {
					ySpeed -= shipAcceleration;
					if (ySpeed < 0) {
						ySpeed = 0;
					}
				} else {
					ySpeed += shipAcceleration;
					if (ySpeed > 0) {
						ySpeed = 0;
					}
				}
				location.y += ySpeed;
			}
		}
		System.out.println(location.x + "," + location.y);
//		System.out.println(xSpeed + "," + ySpeed);
	}
	
	public boolean noKeysHeld() {
		for (int i = 0; i < keysHeld.length; i++) {
			if (keysHeld[i]) {
				return false;
			}
		}
		return true;
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
