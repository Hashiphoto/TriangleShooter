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
		ShipPacket packet = ShipPacket.convertToShipPacket(byteArray);
		if(packet == null) {
			return;
		}
		ship.isFiring = packet.isFiring();
		ship.setLocation(packet.getlocation());
		ship.setDirectionAngle(packet.getRotation());
		ship.firingId = packet.newBulletId();
		if(packet.destroyedBullet() != -1) {
			for(int i = 0; i < bullets.size(); i++) {
				if(bullets.get(i).getId() == packet.destroyedBullet()) {
					bullets.remove(bullets.get(i));
					break;
				}
			}
		}
	}
}
