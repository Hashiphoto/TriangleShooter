package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HostListener extends Thread{
	ServerSocket server;
	Socket socket;
	public HostListener(ServerSocket server) {
		this.server = server;
		socket = null;
	}
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
