package main;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GameGui extends JFrame implements KeyListener{
	private GameDrawingPanel contentPanel;
	private ArrayList<Ship> ships;
	private Ship myShip;
	private GameTime timer;
	
	public GameGui() {
		myShip = new Ship(0, new Point(600, 360));
		ships = new ArrayList<Ship>();
		ships.add(myShip);
		// Iterate through the network and add each ship with its ID
		contentPanel = new GameDrawingPanel(ships);
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
		myShip.setDirection(contentPanel.getMouseLocation());
		// Iterate through each ship and update them
		for(int i = 0; i < ships.size(); i++) {
			if(ships.get(i) == myShip) {
				// Update based on keyboard input
			}
			// Update based on network input
		}
		contentPanel.repaint();
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