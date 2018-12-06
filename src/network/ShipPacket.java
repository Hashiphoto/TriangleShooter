package network;

import java.awt.Point;
import java.nio.ByteBuffer;

/**
 * This is an extension of the Packet class that packages and unpackages data that
 * determines the state of the opponent ship
 * @author Trent
 *
 */
public class ShipPacket extends Packet{	
	boolean isFiring;
	private int x;
	private int y;
	private float rotation;
	private int destroyBullet;
	private int newBulletId;
	private int health;
	private int ammo;
	private double accuracyOffset;
	
	/**
	 * Instantiates a new ShipPacket object
	 * @param isFiring			Whether the ship is firing or not
	 * @param x					The x coordinate of the ship
	 * @param y					The y coordinate of the ship
	 * @param rotation			The angle at which the ship is pointing
	 * @param destroyBullet		The id of the bullet that the ship got hit by
	 * @param newBulletId		The id of the bullet that has been created
	 * @param health			The current health of the ship
	 * @param ammo				The current ammo in the ship
	 * @param accuracyOffset	The adjustment to add to the angle of the fired bullet
	 */
	public ShipPacket(boolean isFiring, int x, int y, float rotation, int destroyBullet, int newBulletId, int health, int ammo, double accuracyOffset){
		this.isFiring = isFiring;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		this.destroyBullet = destroyBullet;
		this.newBulletId = newBulletId;
		this.health = health;
		this.ammo = ammo;
		this.accuracyOffset = accuracyOffset;
		this.packetId = SHIP_PACKET_ID;
	}
	
	/**
	 * Converts all class variables into a byte array to send over the network
	 * The order of the data is the same as the constructror
	 */
	@Override
	public byte[] toByteArray() {
		ByteBuffer buffer = ByteBuffer.allocate(SHIP_PACKET_SIZE);
		byte booleanByte = 0;
		if(isFiring) {
			booleanByte = (byte) 1;
		}
		buffer.put(packetId).put(booleanByte).putInt(x).putInt(y).putFloat(rotation).putInt(destroyBullet).putInt(newBulletId).putInt(health).putInt(ammo).putDouble(accuracyOffset);
		return buffer.array();
	}
	
	public int size() {
		return SHIP_PACKET_SIZE;
	}
	
	public Point getlocation() {
		return new Point(x, y);
	}
	
	public double getRotation() {
		return rotation;
	}
	
	public boolean isFiring() {
		return isFiring;
	}
	
	public int destroyedBullet() {
		return destroyBullet;
	}
	
	public int newBulletId() {
		return newBulletId;
	}
	
	public int getHealth() {
		return health;
	}
	
	public int getAmmo() {
		return ammo;
	}
	
	public double getAccuracyOffset() {
		return accuracyOffset;
	}
}
