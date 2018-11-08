package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Network {
	private Socket clientSocket;
	private ServerSocket serverSocket;
	private String serverIp;
	private InputStream input;
	private OutputStream output;
	
	public Network(String ip) {

	}
	
	public void join(String ip) {
		double initialTime = TimeSeconds.get();
		while (clientSocket == null) {
			if(TimeSeconds.get() - initialTime < Constants.CONNECT_DELAY) {
				continue;
			}
			try {
				clientSocket = new Socket(ip, Constants.PORT);
			} catch (IOException e) {
				System.err.println("Error instantiating client socket");
			}
			initialTime = TimeSeconds.get();
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
				input = opponent.getInputStream();
				output = opponent.getOutputStream();
				System.out.println("Connected to " + opponent.getInetAddress().getHostAddress());
				connections++;
			} catch (IOException e) {
				System.err.println("Server failed to connect to a client");
			}
		}
	}
}
