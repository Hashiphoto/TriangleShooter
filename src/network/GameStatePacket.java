package network;

import java.nio.ByteBuffer;

/**
 * This is an extension of the Packet class that packages and unpackages data that
 * determines different attributes about the current game state, such as losing and
 * picking upgrades
 * @author Trent
 *
 */
public class GameStatePacket extends Packet {
	private byte levelSelect;
	private byte gameAction;
	private byte upgradeSelected;

	/**
	 * Instantiate a new GameStatePacket. use -1 for any parameters that do not matter
	 * @param levelSelect	Which level has been chosen. This is initially picked by the host
	 * and then subsequent levels are picked by the round's winner
	 * @param action		Which action is being performed on the game state, such as losing
	 * @param upgrade		What upgrade has been chosen
	 */
	public GameStatePacket(byte levelSelect, byte action, byte upgrade) {
		this.levelSelect = levelSelect;
		this.gameAction = action;
		this.upgradeSelected = upgrade;
		this.packetId = GAME_PACKET_ID;
	}
	
	/**
	 * Converts all class variables to a byte array to send over the network
	 */
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
