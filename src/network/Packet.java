package network;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * This is the abstract class from which GameStatePacket and ShipPacket derive. It provides the functionality
 * to convert a byte array into Packet objects
 * @author Trent
 *
 */
public abstract class Packet {
	protected static final int SHIP_PACKET_SIZE = 2 + Integer.BYTES * 6 + Float.BYTES + Double.BYTES;
	protected static final int GAME_PACKET_SIZE = 4;
	protected static final byte SHIP_PACKET_ID = 0;
	protected static final byte GAME_PACKET_ID = 1;
	public byte packetId;
	private static ArrayList<Packet> currentPackets = new ArrayList<Packet>();

	/**
	 * Provides the functionality to convert a byte array into an integer
	 * @param source	The byte to read data from. This MUST be at least 4 bytes long
	 * @param start		The point in the byte array to start reading the integer from
	 * @return			An integer version of the 4 bytes in the array from the start
	 */
	protected static int extractInt(byte[]source, int start) {
		return source[start] << 24 | (source[start + 1] & 0xFF) << 16 | (source[start + 2] & 0xFF) << 8 | (source[start + 3] & 0xFF);
	}
	
	/**
	 * Provides the functionality to convert a byte array into an float using the ByteBuffer class
	 * @param source	The byte to read data from. This MUST be at least 4 bytes long
	 * @param start		The point in the byte array to start reading the float from
	 * @return			An float version of the 4 bytes in the array from the start
	 */
	protected static float extractFloat(byte[]source, int start) {
		return ByteBuffer.wrap(source, start, Float.BYTES).getFloat();
	}
	
	/**
	 * Provides the functionality to convert a byte array into a double using the ByteBuffer class
	 * @param source	The byte to read data from. This MUST be at least 8 bytes long
	 * @param start		The point in the byte array to start reading the double from
	 * @return			An double version of the 8 bytes in the array from the start
	 */
	protected static double extractDouble(byte[]source, int start) {
		return ByteBuffer.wrap(source, start, Double.BYTES).getDouble();
	}
	
	/**
	 * This must be overridden to transform the class's data into a byte array
	 * @return	All relevant data as a byte array
	 */
	public abstract byte[] toByteArray();
	
	/**
	 * This method takes in a byte array of any length and parses out individual Packet objects. It reads the first
	 * byte to determine whether it is a ShipPacket or GameStatePacket. Then, it grabs the next bytes of data 
	 * depending on the packet type and puts them into a Packet object. The Packets at the beginning of the array are
	 * the oldest chronologically
	 * @param source	The byte array from the TCP stream
	 * @return	An ArrayList of packets where the oldest packets are at the smallest indices in the ArrayList
	 */
	public static ArrayList<Packet> convertToPacket(byte[] source) {
		currentPackets.clear();
		int i = 0;
		while(i < source.length) {
			Packet packet = null;
			if(source[i] == SHIP_PACKET_ID) {
				boolean isFiring 	= source[1 + i] != 0;
				int x 				= extractInt(source, 2 + i);
				int y 				= extractInt(source, 6 + i);
				float rotation 		= extractFloat(source, 10 + i);
				int destroyBullet 	= extractInt(source, 14 + i);
				int createBullet 	= extractInt(source, 18 + i);
				int health 			= extractInt(source, 22 + i);
				int ammo 			= extractInt(source, 26 + i);
				double accOffset 	= extractDouble(source, 30 + i);
				packet = new ShipPacket(isFiring, x, y, rotation, destroyBullet, createBullet, health, ammo, accOffset);
				
				i += SHIP_PACKET_SIZE;
			}
			else if(source[i] == GAME_PACKET_ID) {
				byte levelSelect 	= source[1 + i];
				byte gameAction 	= source[2 + i];
				byte upgradeSelected= source[3 + i];
				
				packet = new GameStatePacket(levelSelect, gameAction, upgradeSelected);
				i += GAME_PACKET_SIZE;
			}
			else {
				System.err.println("Could not interpret incoming packet");
			}
			if(packet != null) {
				currentPackets.add((Packet)packet);
			}
		}
		return currentPackets;
	}
}
