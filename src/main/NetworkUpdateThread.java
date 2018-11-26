package main;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NetworkUpdateThread extends Thread {
	private Ship ship;
	private Network network;
	private final Lock lock = new ReentrantLock();
	GameTime timer;
	
	public NetworkUpdateThread(Network n, Ship s) {
		ship = s;
		network = n;
	}
	
	@Override
	public void run() {
		timer = new GameTime();
		while(true) {
			if(timer.GetTimeElapsedSeconds() >= 0.01) {
				getUpdates();
				timer.reset();
			}
		}
	}	
	
	public void getUpdates() {
		lock.lock();
		if(network.bytesAvailable() == 0) {
			return;
		}
		try {
			byte[] byteArray = new byte[network.bytesAvailable()];
			network.read(byteArray);
			ShipPacket packet = ShipPacket.convertToShipPacket(byteArray);
			if(packet == null) {
				return;
			}
			ship.isFiring = packet.isFiring();
			ship.setLocation(packet.getlocation());
			ship.setDirectionAngle(packet.getRotation());
		} 
		finally {
			lock.unlock();
		}
	}
}
