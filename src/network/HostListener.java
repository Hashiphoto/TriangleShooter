package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This thread tries to connect to any incoming connection requests by guests. The calling 
 * class can try getSocket() to see if a connection has been established
 * @author Trent
 *
 */
public class HostListener extends Thread{
	ServerSocket server;
	Socket socket;
	
	/**
	 * Instantiate a new HostListener. It must be started using the .start() method inherited
	 * from Thread
	 * @param server
	 */
	public HostListener(ServerSocket server) {
		this.server = server;
		socket = null;
	}
	
	/**
	 * This will start listening for incoming socket connection requests and accept the first
	 * This is a blocking operation and the reason it's in its  own Thread
	 */
	@Override
	public void run() {
		try {
			socket = server.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Socket getSocket() {
		return socket;
	}
}
