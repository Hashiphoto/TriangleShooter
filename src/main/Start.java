package main;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Start {

	public static void main(String[] args) {
		try {
            // Set cross-platform Java L&F (also called "Metal")
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	       // handle exception
	    }
	    catch (ClassNotFoundException e) {
	       // handle exception
	    }
	    catch (InstantiationException e) {
	       // handle exception
	    }
	    catch (IllegalAccessException e) {
	       // handle exception
	    }
		
		NetworkConnectionWindow networkConnector = new NetworkConnectionWindow();
		Network network = networkConnector.getConnection();
		
		GameController game = new GameController();
		game.start();
	}

}
