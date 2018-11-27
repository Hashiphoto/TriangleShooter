package gameElements;

import java.awt.Point;

import gameControl.MathStuffs;
import gameControl.TimeSeconds;

public class Ship {
	private static final int UP = 0;
	private static final int DOWN = 1;
	private static final int LEFT = 2;
	private static final int RIGHT = 3;
	private static final double DEFAULT_SHIP_ROTATION_SPEED = 3;
	private static final double DEFAULT_SHIP_ACCEL = 0.4;
	private static final int DEFAULT_SHIP_MAX_SPEED = 3;
	public static final double DEFAULT_ACCURACY = 1.0;
	public static final double DEFAULT_RELOAD_TIME = 0.5;
	public static final int DEFAULT_CLIP_SIZE = 5;
	public static final int DEFAULT_BULLET_SPEED = 8;
	public static final int DEFAULT_BULLET_RANGE = 300;
	public static final int BULLET_DEATH_DISTANCE = 5;
	
	public boolean isFiring;
	public boolean isEnemy;
	
	private int id;
	private Point location;
	private double rotation;
	private double rotationSpeed;
	private double xSpeed;
	private double ySpeed;
	private int shipMaxSpeed;
	private double shipAcceleration;
	private boolean[] keysHeld;
//	private boolean[] upgrades;
	private int bulletSpeed;
	private int bulletRange;
	private int clipSize;
	private int ammo;
	private double accuracy;
	private double reloadTime;
	private double lastReloaded;
	
	public Ship(int id, Point start) {
		this.isFiring = false;
		this.id = id;
		this.location = start;
		this.rotation = 0;
		this.xSpeed = 0;
		this.ySpeed = 0;
		this.lastReloaded = 0;
		this.keysHeld = new boolean[4];
		this.clipSize = DEFAULT_CLIP_SIZE;
		this.ammo = clipSize;
		this.accuracy = DEFAULT_ACCURACY;
		this.rotationSpeed = DEFAULT_SHIP_ROTATION_SPEED;
		this.shipMaxSpeed = DEFAULT_SHIP_MAX_SPEED;
		this.shipAcceleration = DEFAULT_SHIP_ACCEL;
//		this.upgrades = new boolean[ShipMods.GetNumUpgrades()];
		this.bulletSpeed = DEFAULT_BULLET_SPEED;
		this.bulletRange = DEFAULT_BULLET_RANGE;
		this.reloadTime = DEFAULT_RELOAD_TIME;
	}
	
	public Ship(int id, Point start, int maxSpeed, double acceleration, int bulletSpeed, int bulletRange, int clipSize, double reloadTime) {
		this.isFiring = false;
		this.id = id;
		this.location = start;
		this.rotation = 0;
		this.xSpeed = 0;
		this.ySpeed = 0;
		this.lastReloaded = 0;
		this.keysHeld = new boolean[4];
		this.clipSize = clipSize;
		this.ammo = clipSize;
		this.accuracy = DEFAULT_ACCURACY;
		this.rotationSpeed = DEFAULT_SHIP_ROTATION_SPEED;
		this.shipMaxSpeed = maxSpeed;
		this.shipAcceleration = acceleration;
//		this.upgrades = new boolean[ShipMods.GetNumUpgrades()];
		this.bulletSpeed = bulletSpeed;
		this.bulletRange = bulletRange;
		this.reloadTime = reloadTime;
	}
	
	public void step(Point mouseLocation) {
		move();
		setDirectionMouse(mouseLocation);
		checkReload();
	}
	
	// Movement
	public void keyPressed(String keyCode) {
		switch(keyCode) {
		case "W":
			keysHeld[UP] = true;
			break;
		case "S":
			keysHeld[DOWN] = true;
			break;
		case "A":
			keysHeld[LEFT] = true;
			break;
		case "D":
			keysHeld[RIGHT] = true;
			break;
		}
	}
	
	public void keyReleased(String keyCode) {
		switch(keyCode) {
		case "W":
			keysHeld[UP] = false;
			break;
		case "S":
			keysHeld[DOWN] = false;
			break;
		case "A":
			keysHeld[LEFT] = false;
			break;
		case "D":
			keysHeld[RIGHT] = false;
			break;
		}
	}
	
	public void move() {
		if (keysHeld[UP]) {
			ySpeed -= shipAcceleration;
			if (ySpeed < -shipMaxSpeed) {
				ySpeed = -shipMaxSpeed;
			}
		}
		else if (keysHeld[DOWN]) {
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
		if (keysHeld[LEFT]) {
			xSpeed -= shipAcceleration;
			if (xSpeed < -shipMaxSpeed) {
				xSpeed = -shipMaxSpeed;
			}
		}
		else if (keysHeld[RIGHT]) {
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
		if (ammo > 0 || isEnemy) { // Don't restrict enemy fire
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
	
	public int getShipMaxSpeed() {
		return shipMaxSpeed;
	}
	
	public double getShipAcceleration() {
		return shipAcceleration;
	}
	
//	private boolean[] upgrades;
	
	public int getBulletSpeed() {
		return bulletSpeed;
	}
	
	public int getBulletRange() {
		return bulletRange;
	}
	
	public int getClipSize() {
		return clipSize;
	}

//	private double accuracy;
	
	public double getReloadTime() {
		return reloadTime;
	}
	
	// Sets
	public void setLocation(Point newLocation) {
		if (newLocation == null) {
			return;
		}
		location = newLocation;
	}
}
