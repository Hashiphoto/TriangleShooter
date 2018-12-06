package network;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import gameElements.Ship;
import javafx.animation.Timeline;

public class Network {
	private static final int PORT = 707;
	
	private boolean connected;
	private int id;
	private ServerSocket serverSocket;
	private DataInputStream input;
	private DataOutputStream output;
	private ArrayList<Ship> shipList;
	private Timeline timeline;
	private HostListener listener;
	
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
			return new Ship(id, new Point(x, y));
		} catch (IOException e) {
			System.err.println("Network: Unable to read whole ship");
		}
		return null;
	}
	
	public void sendShipInit(Ship myShip) {
		try {
			output.writeInt(id);
			output.writeInt(myShip.getLocation().x);
			output.writeInt(myShip.getLocation().y);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void sendShipState(Ship s) {
		try {
			ShipPacket packet = new ShipPacket(s.isFiring || s.burstFiring, s.getLocation().x, s.getLocation().y, (float) s.getRotation(), s.hitBy, s.firingId, s.getHealth(), s.getAmmo(), s.accuracyOffset);
			output.write(packet.toByteArray());
		}
		catch(IOException e) {
			System.err.println("Could not reach other client");
		}
	}
	
	public void sendGameInformation(byte level, byte action, byte upgradeSelected) {
		try {
			GameStatePacket gsp = new GameStatePacket(level, action, upgradeSelected);
			output.write(gsp.toByteArray());
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
	
	public boolean isConnected() {
		return connected;
	}
	
	public void setTimeline(Timeline timeline) {
		this.timeline = timeline;
	}
	
	public void join(String ip) {
		Socket socket = null;
		try {
			socket = new Socket(ip, PORT);
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
			// ID for joiner is 1
			id = 1;
			connected = true;
			if(timeline != null) {
				timeline.stop();
			}
		} catch (IOException e) {
			System.err.println("Error instantiating client socket");
		}
	}
	
	public String getMyIp() {
		String myIp = "";
		try {
			InetAddress localhost = InetAddress.getLocalHost();
			myIp = localhost.getHostAddress();
		} catch (UnknownHostException e1) {
			System.err.println("Could not create local host");
		}
		return myIp;
	}
	
	public void host() {
		if(serverSocket == null) {
			try {
				serverSocket = new ServerSocket(PORT);
			} catch (IOException e) {
				System.err.println("Could not instantiate server socket");
				return;
			}
			listener = new HostListener(serverSocket);
			listener.start();
		}
		
		Socket socket = listener.getSocket();
		if(socket != null) {
			try {
				input = new DataInputStream(socket.getInputStream());
				output = new DataOutputStream(socket.getOutputStream());
				// ID for host is 0
				id = 0;
				connected = true;
				if(timeline != null) {
					timeline.stop();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isHosting() {
		return id == 0;
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
