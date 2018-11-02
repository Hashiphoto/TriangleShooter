package main;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GameGui extends JFrame implements KeyListener{
	private JPanel contentPanel;
	private ArrayList<Ship> ships;
	private Ship myShip;
	private GameTime timer;
	
	public GameGui() {
		contentPanel = new JPanel();
		ships = new ArrayList<Ship>();
		// Iterate through the network and add each ship with its ID
		myShip = new Ship(0, new Point(300, 300));
		this.setTitle("Triangle Shooter");
		this.setSize(1280, 720);
		this.setContentPane(contentPanel);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.addKeyListener(this);
	}
	
	public void start() {
		this.setVisible(true);
		timer = new GameTime();
		while(true) {
			if(timer.GetTimeElapsedSeconds() >= 1.0/Constants.FRAMERATE) {
				update();
				timer.reset();
			}
		}
	}
	
	private void update() {
		myShip.move();
		// Iterate through each ship and update them
		for(int i = 0; i < ships.size(); i++) {
			if(ships.get(i) == myShip) {
				myShip.move();
				// Update based on keyboard input
				return;
			}
			// Update based on network input
		}
	}
	
	@Override
	public void keyPressed(KeyEvent key) {
		myShip.keyPressed(key.getKeyCode());		
	}

	@Override
	public void keyReleased(KeyEvent key) {
		myShip.keyReleased(key.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}
	
}