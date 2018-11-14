package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class GameController extends JFrame implements KeyListener, MouseListener {
	private GameDrawingPanel contentPanel;
	private ArrayList<Ship> ships;
	private ArrayList<Bullet> bullets;
	private Ship myShip;
	private GameTime timer;
	private Network network;
	
	public GameController(Network network) {
		this.network = network;
		ships = network.getAllShips();
		myShip = network.getMyShip();
		bullets = new ArrayList<Bullet>();
		ships.add(myShip);
		// Iterate through the network and add each ship with its ID
		contentPanel = new GameDrawingPanel(ships, bullets);
		this.setTitle("Triangle Shooter | ID: " + network.getId());
		this.setSize(1280, 720);
		this.setContentPane(contentPanel);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.addKeyListener(this);
		this.addMouseListener(this);
	}
	
	public void start() {
		this.setVisible(true);
		timer = new GameTime();
		while(true) {
			if(timer.GetTimeElapsedSeconds() >= 1.0/Constants.FRAMERATE) {
				update();
				network.sendShipInit(myShip);
				timer.reset();
			}
		}
	}
	
	private void update() {
		// Iterate through each ship and update them
		for(int i = 0; i < ships.size(); i++) {
			if(ships.get(i) == null) {
				System.err.println(network.getId() + " ship at index " + i + " is null");
			}
			if(ships.get(i) == myShip) {
				// Update based on keyboard input
				myShip.step(contentPanel.getMouseLocation());
				continue;
			}
			// Update based on network input
		}
		
		// Iterate through bullets and update them
		for(int i = 0; i < bullets.size(); i++) {
			if(!bullets.get(i).step()) {
				bullets.remove(i);
			}
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

	@Override
	public void mouseClicked(MouseEvent e) {
		// Left Click
		if(e.getButton() == 1) {
			Bullet bullet = myShip.createBullet();
			if(bullet != null) {
				bullets.add(bullet);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}
	
}