package network;

import java.util.ArrayList;

import gameControl.GameTime;
import gameElements.Bullet;
import gameElements.Ship;

public class NetworkUpdateThread extends Thread {
	private Ship ship;
	private Network network;
	GameTime timer;
	private ArrayList<Bullet> bullets;
	private int level;
	private boolean hasFreshLevel;
	private byte action;
	private boolean hasFreshAction;
	
	public NetworkUpdateThread(Network n, Ship s, ArrayList<Bullet> b) {
		ship = s;
		network = n;
		bullets = b;
		hasFreshLevel = false;
		hasFreshAction = false;
	}
	
	@Override
	public void run() {
		timer = new GameTime();
		while(true) {
			if(timer.GetTimeElapsedSeconds() >= 0.001) {
				getUpdates();
				timer.reset();
			}
		}
	}	
	
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
				if(bullet != null) {
					bullets.add(bullet);
				}
				break;
			
			// Game update packet
			case Packet.GAME_PACKET_ID:
				System.out.println("Got a game packet");
				GameStatePacket currentGame = (GameStatePacket) allPackets.get(i);
				if(currentGame.getLevel() != -1) {
					level = currentGame.getLevel();
					hasFreshLevel = true;
					System.out.println("Has level: " + hasFreshLevel);
				}
				if(currentGame.getAction() != -1) {
					action = currentGame.getAction();
					hasFreshAction = true;
					System.out.println("Has action: " + hasFreshAction);
				}
				break;
			}			
		}
	}
	
	public boolean hasNewLevel() {
		return hasFreshLevel;
	}
	public int getLevel() {
		hasFreshLevel = false;
		return level;
	}
	
	public boolean hasFreshAction() {
		return hasFreshAction;
	}
	public int getAction() {
		hasFreshAction = false;
		return action;
	}
}
