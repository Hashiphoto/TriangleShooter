package network;

import java.util.ArrayList;

import gameControl.GameTime;
import gameElements.Bullet;
import gameElements.Ship;

public class NetworkUpdateThread extends Thread {
	private Ship ship;
	private Network network;
//	private final Lock lock = new ReentrantLock();
	GameTime timer;
	private ArrayList<Bullet> bullets;
	
	public NetworkUpdateThread(Network n, Ship s, ArrayList<Bullet> b) {
		ship = s;
		network = n;
		bullets = b;
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
		ShipPacket[] allPackets = ShipPacket.convertToShipPacket(byteArray);
		if(allPackets == null) {
			System.err.println("WHAT");
			return;
		}
		
		// Set my current location to the most recent incoming packet);
		ShipPacket current = allPackets[allPackets.length - 1];
//		System.out.println(current.getlocation().x + "," + current.getlocation().y);
		ship.setLocation(current.getlocation());
		ship.setDirectionAngle(current.getRotation());
//		ship.isFiring = current.isFiring;
//		ship.firingId = current.newBulletId();
		
//		 Retroactively go through old packets and create bullets received
		for(int i = 0; i < allPackets.length; i++) {
			if(!allPackets[i].isFiring) {
				continue;
			}
			ship.firingId = allPackets[i].newBulletId();
			Bullet bullet = ship.createEnemyBullet();
			if(bullet != null) {
				bullets.add(bullet);
			}
		}
		
		if(current.destroyedBullet() != -1) {
			for(int i = 0; i < bullets.size(); i++) {
				if(bullets.get(i).getId() == current.destroyedBullet()) {
					bullets.remove(bullets.get(i));
					break;
				}
			}
		}
	}
}
