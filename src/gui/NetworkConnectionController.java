package gui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import network.Network;

public class NetworkConnectionController {
	private static final double REFRESH_DELAY = 0.5;
	
	private Network network;
	private Timeline timeline;
	
	@FXML
	private Button hostButton;
	@FXML
	private Button joinButton;
	@FXML
	private TextField ipTextField;
	@FXML
	private TextArea messageText;
	@FXML
	private Button quitButton;
	
	@FXML
	protected void host() {
		hostButton.setDisable(true);
		joinButton.setDisable(true);
		write("Listening for connections on " + network.getMyIp());
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(REFRESH_DELAY), ev -> {
			network.host();
			if(network.isConnected()) {
				write("Connection established!");
			}
		}));
		network.setTimeline(timeline);
	    timeline.setCycleCount(Animation.INDEFINITE);
	    timeline.play();
	}
	
	@FXML
	protected void join() {
		hostButton.setDisable(true);
		joinButton.setDisable(true);
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(REFRESH_DELAY), ev -> {
			write("Attempting to connect to server...");
			network.join(ipTextField.getText());
			if(network.isConnected()) {
				write("Connection established!");
			}
			else {
				write("Attempt failed");
			}
		}));
		network.setTimeline(timeline);
	    timeline.setCycleCount(Animation.INDEFINITE);
	    timeline.play();
	}
	
	public NetworkConnectionController() {
		network = new Network();
	}
	
	public void setTimeline(Timeline timeline) {
		this.timeline = timeline;
	}
	
	public boolean isConnected() {
		if(network.isConnected()) {
			timeline.stop();
		}
		return network.isConnected();
	}
	
	public Network getNetwork() {
		return network;
	}
	
	private void write(String text) {
		messageText.appendText(text + "\n");
	}
}