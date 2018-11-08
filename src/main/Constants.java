package main;

public class Constants {
	public static final int GetNumUpgrades() {
		return Upgrades.values().length;
	}
	
	public enum Upgrades {
		LASER;		//0
	}
	
	// GAME
	public static final int FRAMERATE = 60;
	
	// SHIP
	public static final double DEFAULT_SHIP_ROTATION_SPEED = 5;
	public static final double DEFAULT_SHIP_ACCEL = 0.4;
	public static final double DEFAULT_ACCURACY = 1.0;
	public static final int TURN_PRECISION = 1000;

	public static final int STOP = -1;
	public static final int UP = 0;
	public static final int DOWN = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;
	
	//Bullet
	public static final int DEFAULT_SHIP_MAX_SPEED = 10;
	public static final int DEFAULT_BULLET_SPEED = 6;
	public static final int DEFAULT_BULLET_RANGE = 500;
	public static final int DEFAULT_RELOAD_TIME = 1;
	public static final int BULLET_DEATH_DISTANCE = 5;
}
