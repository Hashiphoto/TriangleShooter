package gameControl;

import java.awt.Point;
import java.util.ArrayList;

import gameElements.Bullet;
import gameElements.Ship;
import gui.GameCanvas;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
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
	private boolean gamePaused;
	private Group root;
	
	public GameController(Network network, Group group, GameCanvas canvas) {
		super(group);
		root = group;
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
		gamePaused = true;
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
				// If a message is displayed on screen, game is paused
				gamePaused = canvas.messageDisplayed();
				update();
			}
		}.start();
		canvas.addMessage(new Message("SYNCHRONIZING...", 4, Color.GRAY));
		canvas.addMessage(new Message("ROUND 1", 3, Color.WHITE));
		canvas.addMessage(new Message("GLORY IN VICTORY", 0.5, Color.RED));
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
			Bullet b = bullets.get(i);
			// Collision checking
			if(b.getId() != myShip.getId()) {
				if(MathStuffs.isCollision(b, myShip)) {
					myShip.setHealth(-10);
					System.out.println(myShip.getHealth());
					bullets.remove(b);
				}
			}
			if(!b.step()) {
				bullets.remove(b);
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
				if(gamePaused) {
					return;
				}
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
				if(gamePaused) {
					return;
				}
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
				if(gamePaused) {
					return;
				}
				String code = event.getCode().toString();
//				input.remove(code);
				myShip.keyReleased(code);
			}
		};
	}
}