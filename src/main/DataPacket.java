package main;

import java.nio.ByteBuffer;

public class DataPacket {
	public static byte[] convertToGameShip(Ship s) {
		ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + Integer.BYTES);
		buffer.putInt(s.getLocation().x);
		buffer.putInt(s.getLocation().y);
		byte[] byteArray = new byte[buffer.remaining()];
		buffer.get(byteArray);
		return byteArray;
	}
	
//	public static byte[] convertToWholeShip(Ship s) {
//		
//	}
}
