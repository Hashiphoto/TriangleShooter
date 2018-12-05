package network;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public abstract class Packet {
	protected static final int SHIP_PACKET_SIZE = 2 + Integer.BYTES * 6 + Float.BYTES;
	protected static final int GAME_PACKET_SIZE = 3;
	protected static final byte SHIP_PACKET_ID = 0;
	protected static final byte GAME_PACKET_ID = 1;
	public byte packetId;
	private static ArrayList<Packet> currentPackets = new ArrayList<Packet>();
	
	protected static int extractInt(byte[]b, int start) {
		return b[start] << 24 | (b[start + 1] & 0xFF) << 16 | (b[start + 2] & 0xFF) << 8 | (b[start + 3] & 0xFF);
	}
	
	protected static float extractFloat(byte[]b, int start) {
//		return b[start] << 24 | (b[start + 1] & 0xFF) << 16 | (b[start + 2] & 0xFF) << 8 | (b[start + 3] & 0xFF);
		return ByteBuffer.wrap(b, start, 4).getFloat();
	}
	
	public abstract byte[] toByteArray();
	
	public static ArrayList<Packet> convertToPacket(byte[] b) {
		currentPackets.clear();
		int i = 0;
		while(i < b.length) {
			Packet packet = null;
			if(b[i] == SHIP_PACKET_ID) {
				boolean isFiring 	= b[1 + i] != 0;
				int x 				= extractInt(b, 2 + i);
				int y 				= extractInt(b, 6 + i);
				float rotation 		= extractFloat(b, 10 + i);
				int destroyBullet 	= extractInt(b, 14 + i);
				int createBullet 	= extractInt(b, 18 + i);
				int health 			= extractInt(b, 22 + i);
				int ammo 			= extractInt(b, 26 + i);
				packet = new ShipPacket(isFiring, x, y, rotation, destroyBullet, createBullet, health, ammo);
				
				i += SHIP_PACKET_SIZE;
			}
			else if(b[i] == GAME_PACKET_ID) {
				byte levelSelect 	= b[1 + i];
				byte gameAction 	= b[2 + i];
//				System.out.println("Recieved a game state packet");
//				System.out.println("\tlevel: " + levelSelect);
//				System.out.println("\taction: " + gameAction);
				
				packet = new GameStatePacket(levelSelect, gameAction);
				i += GAME_PACKET_SIZE;
			}
			else {
				System.err.println("Could not interpret incoming packet");
			}
			if(packet != null) {
				Packet genericPacket = (Packet)packet;
				currentPackets.add((Packet)packet);
			}
		}
		System.out.println();
		return currentPackets;
	}
}
