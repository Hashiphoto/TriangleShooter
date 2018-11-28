package gameControl;

import javafx.scene.paint.Color;

public class Message {
	public String text;
	public double duration;
	public Color color;
	
	// We pretend the duration is in seconds, but we multiply it by 60 because it's in frames
	public Message(String text, double duration, Color color) {
		this.text = text;
		this.duration = 60 * duration;
		this.color = color;
	}
}
