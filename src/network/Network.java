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

/**
 * This class manages the socket connections and sends and receives data. The Packet class itself
 * contains the methods to turn data into byte arrays and back.
 * @author Trent
 *
 */
public class Network {
	// This port was unused
	private static final int PORT = 707;	
	private boolean connected;
	private int id;
	private ServerSocket serverSocket;
	private DataInputStream input;
	private DataOutputStream output;
	private ArrayList<Ship> shipList;
	private Timeline timeline;
	private HostListener listener;
	
	/**
	 * Instantiate a new Network instance
	 */
	public Network() {
		connected = false;
		shipList = new ArrayList<Ship>();
	}

	/**
	 * Get an ArrayList of all ship objects
	 * @return	The ArrayList of Ships 
	 */
	public ArrayList<Ship> getAllShips() {
		return shipList;
	}
	
	/**
	 * @return	The Ship that the local client will control
	 */
	public Ship getMyShip() {
		for(Ship s : shipList) {
			if(s.getId() == id) {
				return s;
			}
		}
		return null;
	}
	
	/**
	 * @return	The ship that isn't the local client's Ship
	 */
	public Ship getOpponent() {
		for(Ship s : shipList) {
			if(s.getId() != id) {
				return s;
			}
		}
		return null;
	}
	
	/**
	 * This method must be called after the connection is established. The host sends its
	 * id and starting point, and the guest receives it. Then, the reverse occurs
	 * @param start0	Ship 0's starting location
	 * @param start1	Ship 1's starting location
	 */
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
	
	/**
	 * In ship initialization, read the next 3 integers as Ship data and instantiate
	 * the enemy ship based on x, y coordinate and id
	 * @return	The enemy Ship
	 */
	private Ship readShip() {
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
	
	/**
	 * In ship initialization, send my ship's x, y coordinate and id number for the 
	 * other client to recreate on their end
	 * @param myShip
	 */
	private void sendShipInit(Ship myShip) {
		try {
			output.writeInt(id);
			output.writeInt(myShip.getLocation().x);
			output.writeInt(myShip.getLocation().y);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Send all relevant information about the player ship. This is called every animation frame
	 * from the GameScene
	 * @param ship	The ship to send the data about
	 */
	public void sendShipState(Ship ship) {
		try {
			ShipPacket packet = new ShipPacket(ship.isFiring || ship.burstFiring, ship.getLocation().x, ship.getLocation().y, (float) ship.getRotation(), ship.hitBy, ship.firingId, ship.getHealth(), ship.getAmmo(), ship.accuracyOffset);
			output.write(packet.toByteArray());
		}
		catch(IOException e) {
			System.err.println("Could not reach other client");
		}
	}
	
	/**
	 * Send information about the game state
	 * @param level				The level that has been selected for the next round
	 * @param action			Any pending actions to take
	 * @param upgradeSelected	The upgrades chosen by the player
	 */
	public void sendGameInformation(byte level, byte action, byte upgradeSelected) {
		try {
			GameStatePacket gsp = new GameStatePacket(level, action, upgradeSelected);
			output.write(gsp.toByteArray());
		}
		catch(IOException e) {
			System.err.println("Could not reach other client");
		}
	}
	
	/**
	 * Get all information in the TCP stream
	 * @param byteArray	This will be filled with the bytes from the TCP stream
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public void read(byte[] byteArray) throws ArrayIndexOutOfBoundsException{
		try {
			input.read(byteArray);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return Returns true if there is at least one byte of data in the TCP stream
	 */
	public int bytesAvailable() {
		try {
			return input.available();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * @return	Returns the id of the player
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @return	Returns true if a TCP connection was established successfully
	 */
	public boolean isConnected() {
		return connected;
	}
	
	/**
	 * This is used to signal to the NetworkConnectionController that a connection has been established
	 * in a non-blocking way
	 * @param timeline	The timeline to stop when a connection is established
	 */
	public void setTimeline(Timeline timeline) {
		this.timeline = timeline;
	}
	
	/**
	 * Attempt to join an existing game at the specified IP Address. 
	 * @param ip	The IP Address of the hosting client
	 */
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
	
	/**
	 * @return	Return the IP Address of the local machine
	 */
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
	
	/**
	 * Start up a HostListener object to listen for incoming connection requests, if
	 * a HostListener has not been started already. This method can be called repeatedly to
	 * check if a connection exists yet.
	 */
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
	
	/**
	 * The player with id 0 is always the host
	 * @return	Returns true if the local id is 0
	 */
	public boolean isHosting() {
		return id == 0;
	}
	
}
