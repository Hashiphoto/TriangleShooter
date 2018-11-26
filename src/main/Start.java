package main;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

public class Start extends Application{

	public static void main(String[] args) {
		launch(args);
	}
	
	public void start(Stage theStage) {
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
		
		Group root = new Group();
		GameCanvas canvas = new GameCanvas(1280, 720);
		GameController game = new GameController(network, root, canvas);
	    theStage.setScene(game);
	    theStage.setTitle("Triangles");
	 
	    root.getChildren().add(canvas);
	    
	    theStage.show();
	    game.start();
	}

}
