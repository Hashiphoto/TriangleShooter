package gui;

import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import gameControl.GameScene;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import network.Network;

public class Start extends Application{
	public static final double REFRESH_DELAY = 0.2;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
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
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/network_connection.fxml"));
		Parent networkRoot = null;
		try {
			networkRoot = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		Stage networkStage = new Stage();
		NetworkConnectionController networkConnector = (NetworkConnectionController) loader.getController();
		networkStage.setScene(new Scene(networkRoot));
		networkStage.setTitle("Triangle Shooter");
		networkStage.setResizable(false);
		networkStage.show();
		
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(REFRESH_DELAY), ev -> {
			if(networkConnector.isConnected()) {
				Network network = networkConnector.getNetwork();
				
				Group gameRoot = new Group();
				GameCanvas canvas = new GameCanvas(1280, 720);
				GameScene game = new GameScene(network, gameRoot, canvas);
				theStage.focusedProperty().addListener((obs, oldVal, newVal) -> game.onLostFocus());
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
			 
			    gameRoot.getChildren().add(canvas);
			    
			    networkStage.hide();
			    theStage.show();
			    game.start();
			}
		}));
		networkConnector.setTimeline(timeline);
	    timeline.setCycleCount(Animation.INDEFINITE);
	    timeline.play();
	}
}
