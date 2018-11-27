package gameControl;

import java.awt.Point;
import java.util.ArrayList;

import gameElements.Bullet;
import gameElements.Ship;
import gui.GameCanvas;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import network.Network;
import network.NetworkUpdateThread;

public class GameController extends Scene {
	public static final int FRAMERATE = 60;
	
	private GameCanvas canvas;
	private ArrayList<Ship> ships;
	private ArrayList<Bullet> bullets;
//	private ArrayList<String> input;
	private Ship myShip;
	private Ship opponent;
	private Network network;
	private NetworkUpdateThread opponentThread;
	private Point mouseLocation;
	
	public GameController(Network network, Group group, GameCanvas canvas) {
		super(group);
		this.network = network;
		this.canvas = canvas;
		ships = network.getAllShips();
		myShip = network.getMyShip();
		opponent = network.getOpponent();
		opponent.isEnemy = true;
		opponentThread = new NetworkUpdateThread(network, opponent);
		bullets = new ArrayList<Bullet>();
		ships.add(myShip);
		canvas.init(ships, bullets);
		mouseLocation = new Point();
		this.setOnMouseMoved(MouseMoved());
		this.setOnMousePressed(MousePressed());
		this.setOnKeyPressed(KeyPressed());
		this.setOnKeyReleased(KeyReleased());
	}
	
	public void start() {
		System.out.println("Game Start!");
		opponentThread.start();
		
		new AnimationTimer() {
			public void handle(long currentNanoTime) {
				update();
			}
		}.start();
	}
	
	private void update() {
		myShip.step(mouseLocation);
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
		canvas.repaint();
		network.sendShipState(myShip);
		myShip.isFiring = false;
	}
	
	public EventHandler<MouseEvent> MouseMoved() {
		return new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mouseLocation.x = (int) event.getX();
				mouseLocation.y = (int) event.getY();
			}
		};
	}
	
	public EventHandler<MouseEvent> MousePressed() {
		return new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton() == MouseButton.PRIMARY) {
					myShip.isFiring = true;
				}
			}
		};
	}
	
	public EventHandler<KeyEvent> KeyPressed() {
		return new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				String code = event.getCode().toString();
//				if(!input.contains(code)) {
//					input.add(code);
//				}
				myShip.keyPressed(code);
			}
		};
	}
	
	public EventHandler<KeyEvent> KeyReleased() {
		return new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				String code = event.getCode().toString();
//				input.remove(code);
				myShip.keyReleased(code);
			}
		};
	}
}