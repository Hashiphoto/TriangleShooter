package network;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import gameControl.TimeSeconds;
import gameElements.Ship;

public class Network {
	private static final int PORT = 707;
	private static final int CONNECT_DELAY = 1;
	
	public boolean connected;
	
	private int id;
	private ServerSocket serverSocket;
	private DataInputStream input;
	private DataOutputStream output;
	private ArrayList<Ship> shipList;
	
	public Network() {
		connected = false;
		shipList = new ArrayList<Ship>();
	}

	public ArrayList<Ship> getAllShips() {
		return shipList;
	}
	
	public Ship getMyShip() {
		for(Ship s : shipList) {
			if(s.getId() == id) {
				return s;
			}
		}
		return null;
	}
	
	public Ship getOpponent() {
		for(Ship s : shipList) {
			if(s.getId() != id) {
				return s;
			}
		}
		return null;
	}
	
	public void initializeShips(Point start0, Point start1) {
		Ship opponent;
		Ship myShip;
		switch(id) {
		// Hosting
		case 0: 
			// Send ship
			myShip = new Ship(id, start0);
			shipList.add(myShip);
			sendShipInit(myShip);
			// Receive ship
			opponent = readShip();
			shipList.add(opponent);
			break;
		// Joining
		case 1: 
			// Receive ship
			opponent = readShip();
			shipList.add(opponent);
			// Send ship
			myShip = new Ship(id, start1);
			sendShipInit(myShip);
			shipList.add(myShip);
			break;
		}
	}
	
	public Ship readShip() {
		try {
			int id = input.readInt();
			int x = input.readInt();
			int y = input.readInt();
			int maxSpeed = input.readInt();
			double shipAccel = input.readDouble();
			int bulletSpeed = input.readInt();
			int bulletRange = input.readInt();
			int clipSize = input.readInt();
			double reloadTime = input.readDouble();
			int health = input.readInt();
			int damage = input.readInt();
			return new Ship(id, new Point(x, y), maxSpeed, shipAccel, bulletSpeed, bulletRange, clipSize, reloadTime, health, damage);
		} catch (IOException e) {
			System.out.println("Network: Unable to read whole ship");
		}
		return null;
	}
	
	public void sendShipInit(Ship myShip) {
		try {
			output.writeInt(id);
			output.writeInt(myShip.getLocation().x);
			output.writeInt(myShip.getLocation().y);
			output.writeInt(myShip.getShipMaxSpeed());
			output.writeDouble(myShip.getShipAcceleration());
			output.writeInt(myShip.getBulletSpeed());
			output.writeInt(myShip.getBulletRange());
			output.writeInt(myShip.getClipSize());
			output.writeDouble(myShip.getReloadTime());
			output.writeInt(myShip.getHealth());
			output.writeInt(myShip.getDamage());
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void sendShipState(Ship s) {
		try {
			ShipPacket packet = new ShipPacket(s.isFiring, s.getLocation().x, s.getLocation().y, (float) s.getRotation());
			output.write(packet.toByteArray());
		}
		catch(IOException e) {
			System.err.println("Could not reach other client");
		}
	}
	
	public void read(byte[] byteArray) throws ArrayIndexOutOfBoundsException{
		try {
			input.read(byteArray);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int bytesAvailable() {
		try {
			return input.available();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void clearInputStream() {
		try {
			input.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getId() {
		return id;
	}
	
	public void join(String ip) {
		Socket socket = null;
		double initialTime = TimeSeconds.get();
		int numTries = 0;
		while (socket == null) {
			if(TimeSeconds.get() - initialTime > CONNECT_DELAY) {
				try {
					System.out.println("Connecting to server... Attempt [" + numTries + "]");
					socket = new Socket(ip, PORT);
					input = new DataInputStream(socket.getInputStream());
					output = new DataOutputStream(socket.getOutputStream());
					// ID for joiner is 1
					id = 1;
					connected = true;
					System.out.println("Connected to server!");
				} catch (IOException e) {
					System.err.println("Error instantiating client socket");
				}

				initialTime = TimeSeconds.get();
				numTries++;
			}
		}
	}
	
	public void host() {
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.err.println("Could not instantiate server socket");
		}
		System.out.println("Host listening for connections");
		
		int maxConnections = 1;
		int connections = 0;
		while (connections < maxConnections) {
			try {
				Socket opponent = serverSocket.accept();
				input = new DataInputStream(opponent.getInputStream());
				output = new DataOutputStream(opponent.getOutputStream());
				System.out.println("Connected to " + opponent.getInetAddress().getHostAddress());
				// ID for host is 0
				id = 0;
				connected = true;
				connections++;
			} catch (IOException e) {
				System.err.println("Server failed to connect to a client");
			}
		}
	}
	
	public void close() {
		try {
			serverSocket.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
