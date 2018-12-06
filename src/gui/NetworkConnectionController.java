package gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import network.Network;

/**
 * This is the controller object for the Host/Join connection dialog. The ip address that the guest
 * joins to should be entered before pressing the Join button, though it is possible to change it while
 * it is searching for connectivity.
 * @author Trent
 *
 */
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
	
	/**
	 * Event bound to the Host button
	 * Opens a TCP port to listen for incoming connection requests and prints the 
	 * local computer's IP Address int he output text field
	 */
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
	
	/**
	 * Event bound to the Join button. Starts searching for connections at the
	 * IP address specified in the textbox attached to the Join button
	 */
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
	
	/**
	 * Event bound to the Quit button. Closes the application
	 */
	@FXML
	protected void quit() {
		Stage window = (Stage) quitButton.getScene().getWindow();
		window.close();
	}
	
	/**
	 * Instantiate a new NetworkConnectionController
	 */
	public NetworkConnectionController() {
		network = new Network();
	}
	
	/**
	 * This is passed in from the main Application and used to signal when a connection has been established
	 * @param timeline
	 */
	public void setTimeline(Timeline timeline) {
		this.timeline = timeline;
	}
	
	/**
	 * @return	Returns true if the client has connected to another client
	 */
	public boolean isConnected() {
		if(network.isConnected()) {
			timeline.stop();
		}
		return network.isConnected();
	}
	
	/**
	 * @return	Return the network object. This should only be done once a connection is established. Use isConnected() to verify this
	 */
	public Network getNetwork() {
		return network;
	}
	
	/**
	 * Outputs a message to the output text field
	 * @param text	The text to print
	 */
	private void write(String text) {
		messageText.appendText(text + "\n");
	}
}