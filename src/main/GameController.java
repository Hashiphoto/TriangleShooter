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
	private Ship opponent;
	private GameTime timer;
	private Network network;
	private NetworkUpdateThread opponentThread;
	
	public GameController(Network network) {
		this.network = network;
		ships = network.getAllShips();
		myShip = network.getMyShip();
		opponent = network.getOpponent();
		opponent.isEnemy = true;
		opponentThread = new NetworkUpdateThread(network, opponent);
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
		System.out.println("Game Start!");
		opponentThread.start();
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
		myShip.step(contentPanel.getMouseLocation());
		if(myShip.isFiring) {
			Bullet bullet = myShip.createBullet();
			if(bullet != null) {
				bullets.add(bullet);
			}
			else {
				myShip.isFiring = false;
			}
		}
		if(opponent.isFiring) {
			Bullet bullet = opponent.createBullet();
			if(bullet != null) {
				bullets.add(bullet);
			}
		}
		
		// Iterate through bullets and update them
		for(int i = 0; i < bullets.size(); i++) {
			if(!bullets.get(i).step()) {
				bullets.remove(i);
			}
		}
		contentPanel.repaint();
		network.sendShipState(myShip);
		myShip.isFiring = false;
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
	public void mousePressed(MouseEvent e) {
		// Left Click
		if(e.getButton() == 1) {
			myShip.isFiring = true;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}
	
}