package network;

import java.awt.Point;
import java.nio.ByteBuffer;

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
