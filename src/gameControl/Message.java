package gameControl;

import javafx.scene.paint.Color;

/**
 * This class is used to create messages to display on screen. They are passed to a GameCanvas
 * and shown on screen for a given duration
 * @author Trent
 */
public class Message {
	public String text;
	public double duration;
	public Color color;
	
	/**
	 * Instantiate a new Message
	 * @param text		The message to write
	 * @param duration	The duration that the message will be on screen, in seconds
	 * @param color		The text color
	 */
	public Message(String text, double duration, Color color) {
		this.text = text;
		this.duration = 60 * duration;
		this.color = color;
	}
}
