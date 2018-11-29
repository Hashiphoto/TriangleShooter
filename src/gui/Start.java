package gui;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import gameControl.GameController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import network.Network;

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
		
		Group root = new Group();
		GameCanvas canvas = new GameCanvas(1280, 720);
		GameController game = new GameController(network, root, canvas);
	    theStage.setScene(game);
	    theStage.setResizable(false);
	    theStage.setTitle("Triangles");
	    theStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
	 
	    root.getChildren().add(canvas);
	    
	    theStage.show();
	    game.start();
	}
}
