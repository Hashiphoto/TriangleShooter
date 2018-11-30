package network;

import java.awt.Point;
import java.nio.ByteBuffer;

public class ShipPacket {
	private static final int SHIP_PACKET_SIZE = 1 + Integer.BYTES * 6 + Float.BYTES;
	
	boolean isFiring;
	private int x;
	private int y;
	private float rotation;
	private int destroyBullet;
	private int newBulletId;
	private int health;
	private int ammo;
	
	public ShipPacket(boolean isFiring, int x, int y, float rotation, int destroyBullet, int newBulletId, int health, int ammo) {
		this.isFiring = isFiring;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		this.destroyBullet = destroyBullet;
		this.newBulletId = newBulletId;
		this.health = health;
		this.ammo = ammo;
	}
	
	public static ShipPacket[] convertToShipPacket(byte[] b) {
		int numPackets = b.length / SHIP_PACKET_SIZE;
		ShipPacket[] allPackets = new ShipPacket[numPackets];
		
		for(int i = 0; i < numPackets; i++) {
			int offset = SHIP_PACKET_SIZE * i;
			
			boolean isFiring 	= b[offset] != 0;
			int x 				= extractInt(b, 1 + offset);
			int y 				= extractInt(b, 5 + offset);
			float rotation 		= extractFloat(b, 9 + offset);
			int destroyBullet 	= extractInt(b, 13 + offset);
			int createBullet 	= extractInt(b, 17 + offset);
			int health 			= extractInt(b, 21 + offset);
			int ammo 			= extractInt(b, 25 + offset);
			
			allPackets[i] = new ShipPacket(isFiring, x, y, rotation, destroyBullet, createBullet, health, ammo);
		}
		
		return allPackets;
	}
	
	public byte[] toByteArray() {
		ByteBuffer buffer = ByteBuffer.allocate(SHIP_PACKET_SIZE);
		byte booleanByte = 0;
		if(isFiring) {
			booleanByte = (byte) 1;
		}
		buffer.put(booleanByte).putInt(x).putInt(y).putFloat(rotation).putInt(destroyBullet).putInt(newBulletId).putInt(health).putInt(ammo);
		return buffer.array();
	}
	
	private static int extractInt(byte[]b, int start) {
		return b[start] << 24 | (b[start + 1] & 0xFF) << 16 | (b[start + 2] & 0xFF) << 8 | (b[start + 3] & 0xFF);
	}
	
	private static float extractFloat(byte[]b, int start) {
//		return b[start] << 24 | (b[start + 1] & 0xFF) << 16 | (b[start + 2] & 0xFF) << 8 | (b[start + 3] & 0xFF);
		return ByteBuffer.wrap(b, start, 4).getFloat();
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
}
