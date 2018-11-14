package main;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Network {
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
	
	public void initializeShips() {
		switch(id) {
		// Hosting
		case 0: 
			try {
				// Send ship
				Ship myShip = new Ship(id, new Point(300, 300));
				shipList.add(myShip);
				output.writeInt(id);
				output.writeInt(myShip.getLocation().x);
				output.writeInt(myShip.getLocation().y);
				output.flush();
				// Receive ship
				int opponentId = input.readInt();
				int x = input.readInt();
				int y = input.readInt();
				shipList.add(new Ship(opponentId, new Point(x, y)));
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		// Joining
		case 1: 
			try {
				// Receive ship
				int opponentId = input.readInt();
				int x = input.readInt();
				int y = input.readInt();
				shipList.add(new Ship(opponentId, new Point(x, y)));
				// Send ship
				Ship myShip = new Ship(id, new Point(600, 300));
				output.writeInt(id);
				output.writeInt(myShip.getLocation().x);
				output.writeInt(myShip.getLocation().y);
				output.flush();
				shipList.add(myShip);
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		}
	}
	
	public void sendShipInit(Ship s) {
		
	}
	
	public void sendShipState(Ship s) {
		byte[] data = DataPacket.convertToGameShip(s);
		try {
			output.write(data);
			output.flush();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
//	public Point getOpponentShipLocation() {
//		input.read(arg0)
//	}
	
	public int getId() {
		return id;
	}
	
	public void join(String ip) {
		Socket socket = null;
		double initialTime = TimeSeconds.get();
		int numTries = 0;
		while (socket == null) {
			if(TimeSeconds.get() - initialTime > Constants.CONNECT_DELAY) {
				try {
					System.out.println("Connecting to server... Attempt [" + numTries + "]");
					socket = new Socket(ip, Constants.PORT);
					System.out.println("Socket created");
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
			serverSocket = new ServerSocket(Constants.PORT);
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
