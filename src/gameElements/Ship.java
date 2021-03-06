package gameElements;

import java.awt.Point;

import gameControl.MathStuffs;

/**
 * The Ship class contains all information about a Ship
 * @author Trent
 *
 */
public class Ship {
	public static final int DEFAULT_HEALTH = 100;
	public static final int DEFAULT_SHIP_MAX_SPEED = 5;
	public static final double DEFAULT_ACCURACY = 0.04;
	public static final int DEFAULT_BULLET_RANGE = 450;
	public static final int DEFAULT_BULLET_SIZE = 14;
	public static final int DEFAULT_CLIP_SIZE = 3;
	public static final int DEFAULT_RELOAD_TIME = 700; // milliseconds
	private static final int UP = 0;
	private static final int DOWN = 1;
	private static final int LEFT = 2;
	private static final int RIGHT = 3;
	private static final double DEFAULT_SHIP_ROTATION_SPEED = 3;
	private static final double DEFAULT_SHIP_ACCEL = 0.2;
	private static final int DEFAULT_BULLET_DAMAGE = 10;
	private static final int DEFAULT_BULLET_SPEED = 12;
	private static final int DEFAULT_SHIP_RADIUS = 30;
	
	public boolean isFiring;
	public boolean isEnemy;
	public int hitBy;
	public int firingId;
	public double accuracyOffset;
	public boolean burstFiring;
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
	private int maxHealth;
	private int bulletSize;
	private int radius;
	
	/**
	 * Create a new Ship instance
	 * @param id	The id of the ship
	 * @param start	The starting location of the ship
	 */
	public Ship(int id, Point start) {
		this.id = id;
		this.location = start;
		this.start = new Point(start.x, start.y);
		hardReset();
	}
	
	/**
	 * Iterate one game tick. This moves and turns the ship by the appropriate amounts.
	 *  It also updates the Ship's reload state.
	 * @param mouseLocation
	 */
	public void step(Point mouseLocation) {
		move();
		setDirectionMouse(mouseLocation);
		checkReload();
	}
	
	/**
	 * Reset the ship between rounds
	 */
	public void reset() {
		location.x = start.x;
		location.y = start.y;
		health = maxHealth;
		ammo = clipSize;
	}
	
	/**
	 * Completely reset the ship between games
	 */
	public void hardReset() {
		this.isFiring = false;
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
		this.damage = DEFAULT_BULLET_DAMAGE;
		this.health = DEFAULT_HEALTH;
		this.maxHealth = health;
		this.bulletSize = DEFAULT_BULLET_SIZE;
		this.radius = DEFAULT_SHIP_RADIUS;
		accuracyOffset = 0;
		hitBy = -1;
		firingId = -1;
		burstFiring = false;
	}
	
	// Movement ///////////////////////////////////////////////////////////////
	
	/**
	 * Track which keys are being held down
	 * @param keyCode	The keyCode of the key pressed
	 */
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
	
	
	/**
	 * Track which keys have been released
	 * @param keyCode	The keyCode of the key pressed
	 */
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
	
	/**
	 * Advance the ship's position based on what keys are being held
	 */
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
	
	/**
	 * Stops the ship movement
	 */
	public void stop() {
		xSpeed = 0;
		ySpeed = 0;
		releaseKeys();
	}
	
	/**
	 * Simulates letting go of all key presses
	 */
	public void releaseKeys() {
		for(int i = 0; i < keysHeld.length; i++) {
			keysHeld[i] = false;
		}
	}
	
	/**
	 * Set the direction of the ship based on mouse position
	 * @param mouse	The mouse location
	 */
	public void setDirectionMouse(Point mouse) {
		rotation = MathStuffs.turnShipToAngle(location, mouse, rotation, rotationSpeed);
	}
	
	/**
	 * Set the angle of the Ship directly
	 * @param angle	The angle for the ship to point
	 */
	public void setDirectionAngle(Double angle) {
		rotation = angle;
	}
	
	/**
	 * This must be called every game frame. It checks if enough time has passed to add
	 * another bullet to the clip and increases ammo by one if so
	 */
	public void checkReload() {
		long currentTimeSec = System.currentTimeMillis();
		if (ammo < clipSize && currentTimeSec > reloadTime + lastReloaded) {
			ammo++;
			lastReloaded = currentTimeSec;
		}
	}
	
	/**
	 * Creates a bullet instance based on the ship's current shooting settings. If the ship has
	 * no ammo, it returns null
	 * @return	A new bullet instance
	 */
	public Bullet createBullet() {
		Bullet newBullet = null;
		if (ammo > 0) {
			// Don't reload immediately after firing the first shot
			if(ammo == clipSize) {
				lastReloaded = System.currentTimeMillis();
			}
			accuracyOffset = getNewAccuracyOffset();
			newBullet = new Bullet(id, new Point(location), rotation + accuracyOffset, bulletSpeed, bulletRange, accuracy, damage, bulletSize);
			firingId = newBullet.getId();
			ammo--;
		}
		return newBullet;
	}
	
	/**
	 * This method is similar to createBullet() but is not restricted by ammo. It will always return
	 * a new Bullet instance
	 * @return
	 */
	public Bullet createEnemyBullet() {
		if(ammo == clipSize) {
			lastReloaded = System.currentTimeMillis();
		}
		ammo--;
		return new Bullet(id, new Point(location), rotation, bulletSpeed, bulletRange, accuracy, damage, bulletSize, firingId);
	}
	
	/**
	 * This method generates a random number from -Pi/2 to PI/2 and multiplied by accuracy. It
	 * always returns 0 when accuracy is 0.0
	 * @return	An offset for the new Bullet angle, in radians
	 */
	private double getNewAccuracyOffset() {
		return accuracy * ((Math.random() - 0.5) * Math.PI);
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
	
	public int getMaxSpeed() {
		return shipMaxSpeed;
	}
	
	public double getAcceleration() {
		return shipAcceleration;
	}
	
	public double getRotationSpeed() {
		return rotationSpeed;
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

	public double getAccuracy() {
		return accuracy;
	}
	
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
	
	public int getMaxHealth() {
		return maxHealth;
	}
	
	public int getAmmo() {
		return ammo;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public int getBulletSize() {
		return bulletSize;
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
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	public void setMaxSpeed(int speed) {
		shipMaxSpeed = speed;
		if(shipMaxSpeed < 0) {
			shipMaxSpeed = 0;
		}
	}
	
	public void setAcceleration(double accel) {
		shipAcceleration = accel;
		if(shipAcceleration < 0) {
			shipAcceleration = 0;
		}
	}
	
	public void setRotationSpeed(double rotSpeed) {
		rotationSpeed = rotSpeed;
		if(rotationSpeed < 1) {
			rotationSpeed = 1;
		}
	}
	
	public void setAmmo(int ammo) {
		this.ammo = ammo;
		if(ammo < 1) {
			ammo = 1;
		}
	}
	
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
		if(this.accuracy < 0) {
			this.accuracy = 0;
		}
	}
	
	public void setBulletRange(int range) {
		bulletRange = range;
		if(bulletRange < 0) {
			bulletRange = 0;
		}
	}
	
	public void setBulletSpeed(int speed) {
		this.bulletSpeed = speed;
	}
	
	public void setDamage(int dmg) {
		damage = dmg;
		if(damage < 1) {
			damage = 1;
		}
	}
	
	public void setClipSize(int size) {
		clipSize = size;
		if(clipSize < 1) {
			clipSize = 1;
		}
	}
	
	public void setReloadTime(int millis) {
		reloadTime = millis;
		if(reloadTime < 0) {
			reloadTime = 0;
		}
	}
	
	public double getPercentageReload() {
		double percentage = 1.0;
		if(ammo < clipSize) {
			percentage = (System.currentTimeMillis() - lastReloaded) / reloadTime;
			if(percentage > 1.0) {
				percentage = 1.0;
			}
		}
		return percentage;
	}
	
	public void setBulletSize(int size) {
		bulletSize = size;
		if(bulletSize < 8) {
			bulletSize = 8;
		}
	}
}
