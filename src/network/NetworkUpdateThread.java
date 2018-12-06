package network;

import java.util.ArrayList;

import gameElements.Bullet;
import gameElements.Ship;

/**
 * This class runs constantly in the background and updates the enemy ship and game state
 * variables based on incoming TCP packets from the opponent
 * @author Trent
 *
 */
public class NetworkUpdateThread extends Thread {
	private Ship ship;
	private Network network;
	private ArrayList<Bullet> bullets;
	private byte level;
	private boolean hasFreshLevel;
	private byte action;
	private boolean hasFreshAction;
	private byte upgrade;
	private boolean hasFreshUpgrade;
	
	/**
	 * Instantiate a new Network Update Thread. start() must be called to begin listening
	 * for updates
	 * @param network	The network instance. It must be connected to another client already
	 * @param ship		The ship that will be updated based on network input
	 * @param bullets	The bullet ArrayList that contains all Bullets in the game
	 */
	public NetworkUpdateThread(Network network, Ship ship, ArrayList<Bullet> bullets) {
		this.ship = ship;
		this.network = network;
		this.bullets = bullets;
		this.hasFreshLevel = false;
		this.hasFreshAction = false;
		this.hasFreshUpgrade = false;
	}
	
	/**
	 * Start listening for incoming updates. This Thread must be running for the entire duration
	 * of the program. It checks for updates every 1 millisecond so as to not overload the CPU
	 */
	@Override
	public void run() {
		long lastChecked = System.currentTimeMillis();
		while(true) {
			if(System.currentTimeMillis() - lastChecked >= 1) {
				getUpdates();
				lastChecked = System.currentTimeMillis();
			}
		}
	}	
	
	/**
	 * Gets all the bytes in the TCP stream and parses them. ShipPackets are used to update the 
	 * ship, and GameStatePackets are used to update the data visible to the GameScene
	 */
	public void getUpdates() {
		if(network.bytesAvailable() == 0) {
			return;
		}
		byte[] byteArray = new byte[network.bytesAvailable()];
		network.read(byteArray);
		ArrayList<Packet> allPackets = Packet.convertToPacket(byteArray);
		if(allPackets == null) {
			System.err.println("Network Update Thread recieved empty data");
			return;
		}
		
		// Iterate backwards through the data, most recent to oldest
		boolean setCurrent = false;
		for(int i = allPackets.size() - 1; i >= 0; i--) {
			switch(allPackets.get(i).packetId) {
			// Ship update packet
			case Packet.SHIP_PACKET_ID: 
				ShipPacket shipCurrent = (ShipPacket) allPackets.get(i);
				// Only take this info for the newest packet
				if(!setCurrent) {
					setCurrent = true;
					ship.setLocation(shipCurrent.getlocation());
					ship.setDirectionAngle(shipCurrent.getRotation());
					ship.setHealth(shipCurrent.getHealth());
					ship.setAmmo(shipCurrent.getAmmo());
				}
				// Check if the ship was hit by any bullets and remove them from the game
				if(shipCurrent.destroyedBullet() != -1) {
					for(int j = 0; j < bullets.size(); j++) {
						if(bullets.get(j).getId() == shipCurrent.destroyedBullet()) {
							bullets.remove(bullets.get(j));
							break;
						}
					}
				}
				// Create the bullets
				if(!shipCurrent.isFiring) {
					continue;
				}
				ship.firingId = shipCurrent.newBulletId();
				Bullet bullet = ship.createEnemyBullet();
				bullet.setOffset(shipCurrent.getAccuracyOffset());
				if(bullet != null) {
					bullets.add(bullet);
				}
				break;
			
			// Game update packet
			case Packet.GAME_PACKET_ID:
				GameStatePacket currentGame = (GameStatePacket) allPackets.get(i);
				if(currentGame.getLevel() != -1) {
					level = currentGame.getLevel();
					hasFreshLevel = true;
				}
				if(currentGame.getAction() != -1) {
					action = currentGame.getAction();
					hasFreshAction = true;
				}
				if(currentGame.getUpgrade() != -1) {
					upgrade = currentGame.getUpgrade();
					hasFreshUpgrade = true;
				}
				break;
			}			
		}
	}
	
	/**
	 * @return	True if the level has not been returned yet
	 */
	public boolean hasNewLevel() {
		return hasFreshLevel;
	}
	
	/**
	 * @return	Returns the last level received
	 */
	public int getLevel() {
		hasFreshLevel = false;
		return level;
	}
	
	/**
	 * @return	True if the latest action has not been returned yet
	 */
	public boolean hasFreshAction() {
		return hasFreshAction;
	}
	
	/**
	 * @return	Returns the latest action received
	 */
	public int getAction() {
		hasFreshAction = false;
		return action;
	}
	
	/**
	 * @return	True if the latest upgrade has not yet been returned
	 */
	public boolean hasFreshUpgrade() {
		return hasFreshUpgrade;
	}
	
	/**
	 * @return	Returns the latest upgrade received
	 */
	public int getUpgrade() {
		hasFreshUpgrade = false;
		return upgrade;
	}
}
