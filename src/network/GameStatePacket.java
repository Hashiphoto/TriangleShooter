package network;

import java.nio.ByteBuffer;

public class GameStatePacket extends Packet {
	private byte levelSelect;
	private byte gameAction;
	private byte upgradeSelected;

	public GameStatePacket(byte levelSelect, byte action, byte upgrade) {
		this.levelSelect = levelSelect;
		this.gameAction = action;
		this.upgradeSelected = upgrade;
		this.packetId = GAME_PACKET_ID;
	}
	
	@Override
	public byte[] toByteArray() {
		ByteBuffer buffer = ByteBuffer.allocate(GAME_PACKET_SIZE);
		buffer.put(packetId).put(levelSelect).put(gameAction).put(upgradeSelected);
		return buffer.array();
	}
	
	public byte getLevel() {
		return levelSelect;
	}
	
	public byte getAction() {
		return gameAction;
	}
	
	public byte getUpgrade() {
		return upgradeSelected;
	}
}
