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
	private int clipSize;
	private int ammo;
	private double accuracy;
	private double reloadTime;
	private double lastReloaded;
	
	public Ship(int id, Point start) {
		this.id = id;
		this.location = start;
		this.rotation = 0;
		this.xSpeed = 0;
		this.ySpeed = 0;
		this.lastReloaded = 0;
		this.keysHeld = new boolean[4];
		this.clipSize = Constants.DEFAULT_CLIP_SIZE;
		this.ammo = clipSize;
		this.accuracy = Constants.DEFAULT_ACCURACY;
		this.rotationSpeed = Constants.DEFAULT_SHIP_ROTATION_SPEED;
		this.shipMaxSpeed = Constants.DEFAULT_SHIP_MAX_SPEED;
		this.shipAcceleration = Constants.DEFAULT_SHIP_ACCEL;
		this.upgrades = new boolean[Constants.GetNumUpgrades()];
		this.bulletSpeed = Constants.DEFAULT_BULLET_SPEED;
		this.bulletRange = Constants.DEFAULT_BULLET_RANGE;
		this.reloadTime = Constants.DEFAULT_RELOAD_TIME;
	}
	
	public void step(Point mouseLocation) {
		move();
		setDirectionMouse(mouseLocation);
		checkReload();
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
	
	public void setDirectionMouse(Point mouse) {
		rotation = MathStuffs.calculateNewAngle(location, mouse, rotation, rotationSpeed);
	}
	
	public void setDirectionAngle(Double angle) {
		rotation = angle;
	}
	
	// Shooting
	private void checkReload() {
//		System.out.println("Ammo: " + ammo);
		double currentTimeSec = TimeSeconds.get();
		if (ammo < clipSize && currentTimeSec - lastReloaded > reloadTime) {
			ammo++;
			lastReloaded = currentTimeSec;
		}
	}
	
	public Bullet createBullet() {
		Bullet newBullet = null;
		if (ammo > 0) {
			// Don't reload immediately after firing the first shot
			if(ammo == clipSize) {
				lastReloaded = TimeSeconds.get();
			}
			newBullet = new Bullet(id, new Point(location), rotation, bulletSpeed, bulletRange, accuracy);
			ammo--;
		}
		return newBullet;
	}
	
	// Gets	
	public int getId() {
		return id;
	}
	
	public Point getLocation() {
		return location;
	}
	
	public double getRotation() {
		return rotation;
	}
	
	// Sets
	public void setLocation(Point newLocation) {
		if (newLocation == null) {
			return;
		}
		location = newLocation;
	}
}
