package main;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class GameController extends JFrame implements KeyListener, MouseListener{
	private GameDrawingPanel contentPanel;
	private ArrayList<Ship> ships;
	private ArrayList<Bullet> bullets;
	private Ship myShip;
	private GameTime timer;
	
	public GameController() {
		myShip = new Ship(0, new Point(600, 360));
		ships = new ArrayList<Ship>();
		bullets = new ArrayList<Bullet>();
		ships.add(myShip);
		// Iterate through the network and add each ship with its ID
		contentPanel = new GameDrawingPanel(ships, bullets);
		this.setTitle("Triangle Shooter");
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
		
		// Iterate through bullets and update them
		int numBullets = bullets.size();
		for(int i = 0; i < numBullets; i++) {
			bullets.get(i).step();
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
			bullets.add(myShip.createBullet());
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