package main;

import java.awt.Point;
import java.nio.ByteBuffer;

public class ShipPacket {
	private static final int SHIP_PACKET_SIZE = 1 + Integer.BYTES * 2 + Float.BYTES;
	
	boolean isFiring;
	private int x;
	private int y;
	private float rotation;
	private int size;
	
	public ShipPacket(boolean isFiring, int x, int y, float rotation) {
		this.isFiring = isFiring;
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		size = SHIP_PACKET_SIZE;
	}
	
	public static ShipPacket convertToShipPacket(byte[] b) {
		// If received more than one packet, grab the latest position/rotation only
		// BUT make sure to acknowledge if a bullet was fired
		if(b.length > SHIP_PACKET_SIZE) {
//			System.out.println("Recieved more than one byte");
			byte[] newByteArr;
			newByteArr = new byte[SHIP_PACKET_SIZE];
			for(int i = 0; i < SHIP_PACKET_SIZE; i++) {
				newByteArr[i] = b[b.length - SHIP_PACKET_SIZE + i];
			}
			
			// Iterate through all first bits to see if a bullet was fired in the time span
			if(newByteArr[0] == 0) {
				for(int i = 0; i < b.length; i += SHIP_PACKET_SIZE) {
					if (b[i] == 1) {
						newByteArr[0] = 1;
						break;
					}
				}
			}
			b = newByteArr;
		}
		boolean isFiring = b[0] != 0;
		int x = extractInt(b, 1);
		int y = extractInt(b, 5);
		float rotation = extractFloat(b, 9);
		
		return new ShipPacket(isFiring, x, y, rotation);
	}
	
	public byte[] toByteArray() {
		ByteBuffer buffer = ByteBuffer.allocate(size);
		byte booleanByte = 0;
		if(isFiring) {
			booleanByte = (byte) 1;
		}
		buffer.put(booleanByte).putInt(x).putInt(y).putFloat(rotation);
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
		return size;
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
}
