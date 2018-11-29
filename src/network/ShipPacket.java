package network;

import java.awt.Point;
import java.nio.ByteBuffer;

public class ShipPacket {
	private static final int SHIP_PACKET_SIZE = 1 + Integer.BYTES * 4 + Float.BYTES;
	
	boolean isFiring;
	private int x;
	private int y;
	private float rotation;
	private int destroyBullet;
	private int newBulletId;
	
	public ShipPacket(boolean isFiring, int x, int y, float rotation, int destroyBullet, int newBulletId) {
		this.isFiring = isFiring;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		this.destroyBullet = destroyBullet;
		this.newBulletId = newBulletId;
	}
	
	public static ShipPacket[] convertToShipPacket(byte[] b) {
		int numPackets = b.length / SHIP_PACKET_SIZE;
		ShipPacket[] allPackets = new ShipPacket[numPackets];
		
		for(int i = 0; i < numPackets; i++) {
			boolean isFiring 	= b[SHIP_PACKET_SIZE * i] != 0;
			int x 				= extractInt(b, 1 + SHIP_PACKET_SIZE * i);
			int y 				= extractInt(b, 5 + SHIP_PACKET_SIZE * i);
			float rotation 		= extractFloat(b, 9 + SHIP_PACKET_SIZE * i);
			int destroyBullet 	= extractInt(b, 13 + SHIP_PACKET_SIZE * i);
			int createBullet 	= extractInt(b, 17 + SHIP_PACKET_SIZE * i);
			allPackets[i] = new ShipPacket(isFiring, x, y, rotation, destroyBullet, createBullet);
		}
		
		return allPackets;
	}
	
	public byte[] toByteArray() {
		ByteBuffer buffer = ByteBuffer.allocate(SHIP_PACKET_SIZE);
		byte booleanByte = 0;
		if(isFiring) {
			booleanByte = (byte) 1;
		}
		buffer.put(booleanByte).putInt(x).putInt(y).putFloat(rotation).putInt(destroyBullet).putInt(newBulletId);
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
}
