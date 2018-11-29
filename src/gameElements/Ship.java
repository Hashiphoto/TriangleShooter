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
	private static final double DEFAULT_ACCURACY = 1.0;
	private static final double DEFAULT_RELOAD_TIME = 0.5;
	private static final int DEFAULT_CLIP_SIZE = 5;
	private static final int DEFAULT_BULLET_SPEED = 8;
	private static final int DEFAULT_BULLET_RANGE = 300;
	private static final int DEFAULT_BULLET_DAMAGE = 10;
	private static final int DEFAULT_HEALTH = 100;
	
	public boolean isFiring;
	public boolean isEnemy;
	public int hitBy;
	public int firingId;
	public static final String[] Name = {
		"BLUE", "ORANGE"
	};
	
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
	private int damage;
	private int health;
	private Point start;
	
	public Ship(int id, Point start) {
		this(id, start, DEFAULT_SHIP_MAX_SPEED, DEFAULT_SHIP_ACCEL, DEFAULT_BULLET_SPEED, 
				DEFAULT_BULLET_RANGE, DEFAULT_CLIP_SIZE, DEFAULT_RELOAD_TIME, DEFAULT_HEALTH, DEFAULT_BULLET_DAMAGE);
	}

	public Ship(int id, Point start, int maxSpeed, double acceleration, int bulletSpeed, 
			int bulletRange, int clipSize, double reloadTime, int health, int damage) {
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
		this.damage = damage;
		this.health = health;
		this.start = start;
		hitBy = -1;
		firingId = -1;
	}
	
	public void step(Point mouseLocation) {
		move();
		setDirectionMouse(mouseLocation);
		checkReload();
	}
	
	public void reset() {
		this.location = start;
		this.health = DEFAULT_HEALTH;
	}
	
	// Movement ///////////////////////////////////////////////////////////////
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
	}
	
	public void setDirectionMouse(Point mouse) {
		rotation = MathStuffs.calculateNewAngle(location, mouse, rotation, rotationSpeed);
	}
	
	public void setDirectionAngle(Double angle) {
		rotation = angle;
	}
	
	// Shooting
	private void checkReload() {
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
			newBullet = new Bullet(id, new Point(location), rotation, bulletSpeed, bulletRange, accuracy, damage);
			firingId = newBullet.getId();
			ammo--;
		}
		return newBullet;
	}
	
	public Bullet createEnemyBullet() {
		return new Bullet(id, new Point(location), rotation, bulletSpeed, bulletRange, accuracy, damage, firingId);
	}
	
	// Gets	///////////////////////////////////////////////////////////////////
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
	
	public int getDamage() {
		return damage;
	}
	
	public int getHealth() {
		return health;
	}
	
	public boolean isDead() {
		return health <= 0;
	}
	
	// Sets ///////////////////////////////////////////////////////////////////
	public void setLocation(Point newLocation) {
		if (newLocation == null) {
			return;
		}
		location = newLocation;
	}
	
	public void takeDamage(int damage) {
		health -= damage;
	}
	
	public void setHealth(int h) {
		health = h;
	}
}
