package main;

import java.awt.Point;
import java.nio.ByteBuffer;

public class ShipPacket {
	private int x;
	private int y;
	private float rotation;
	private int size;
	
	public ShipPacket(int x, int y, float rotation) {
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		size = Constants.SHIP_PACKET_SIZE;
	}
	
	public static ShipPacket convertToShipPacket(byte[] b) {
		if(b.length > Constants.SHIP_PACKET_SIZE) {
			byte[] newByteArr;
			newByteArr = new byte[12];
			for(int i = 0; i < 12; i++) {
				newByteArr[i] = b[b.length - 12 + i];
			}
			b = newByteArr;
		}
		byte[] temp = new byte[4];
		int x = 0;
		int y = 0;
		float rotation = 0;
		for(int i = 0; i < b.length; i++) {
			switch(i) {
			case 0:
				temp[i] = b[i];
				break;
			case 1:
				temp[i] = b[i];
				break;
			case 2:
				temp[i] = b[i];
				break;
			case 3:
				temp[i] = b[i];
				x = ByteBuffer.wrap(temp).getInt();
				break;
			case 4:
				temp[i % 4] = b[i];
				break;
			case 5:
				temp[i % 4] = b[i];
				break;
			case 6:
				temp[i % 4] = b[i];
				break;
			case 7:
				temp[i % 4] = b[i];
				y = ByteBuffer.wrap(temp).getInt();
				break;
			case 8:
				temp[i % 4] = b[i];
				break;
			case 9:
				temp[i % 4] = b[i];
				break;
			case 10:
				temp[i % 4] = b[i];
				break;
			case 11:
				temp[i % 4] = b[i];
				rotation = ByteBuffer.wrap(temp).getFloat();
				break;
			}
		}
		return new ShipPacket(x, y, rotation);
	}
	
	public byte[] toByteArray() {
		ByteBuffer buffer = ByteBuffer.allocate(size);
		buffer.putInt(x).putInt(y).putFloat(rotation);
		return buffer.array();
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
}
