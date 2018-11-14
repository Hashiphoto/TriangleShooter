package main;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Start {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	       e.printStackTrace();
	    }
	    catch (ClassNotFoundException e) {
	       e.printStackTrace();
	    }
	    catch (InstantiationException e) {
	       e.printStackTrace();
	    }
	    catch (IllegalAccessException e) {
	       e.printStackTrace();
	    }
		
		NetworkConnectionWindow networkConnector = new NetworkConnectionWindow();
		Network network = networkConnector.getConnection();
		network.initializeShips();
		
		GameController game = new GameController(network);
		game.start();
	}

}
