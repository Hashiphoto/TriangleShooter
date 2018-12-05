package network;

import java.nio.ByteBuffer;

public class GameStatePacket extends Packet {
	
	private byte levelSelect;
	private byte gameAction;

	public GameStatePacket(byte levelSelect, byte action) {
		this.levelSelect = levelSelect;
		this.gameAction = action;
		this.packetId = GAME_PACKET_ID;
	}
	
	@Override
	public byte[] toByteArray() {
		ByteBuffer buffer = ByteBuffer.allocate(GAME_PACKET_SIZE);
		buffer.put(packetId).put(levelSelect).put(gameAction);
		return buffer.array();
	}
	
	public byte getLevel() {
		return levelSelect;
	}
	
	public byte getAction() {
		return gameAction;
	}
}
