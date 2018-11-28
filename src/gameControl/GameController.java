package gameControl;

import java.awt.Point;
import java.util.ArrayList;

import gameElements.Bullet;
import gameElements.BulletCounter;
import gameElements.Ship;
import gui.GameCanvas;
import gui.Scoreboard;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import network.Network;
import network.NetworkUpdateThread;

public class GameController extends Scene {
	public static final int FRAMERATE = 60;
	private static final int ROUNDS = 7;
	
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
	private Scoreboard scoreboard;
	
	public GameController(Network network, Group group, GameCanvas canvas) {
		super(group);
		root = group;
		this.network = network;
		this.canvas = canvas;
		network.initializeShips(new Point(200, (int) (canvas.getHeight() / 2)),  new Point((int) canvas.getWidth() - 200, (int) (canvas.getHeight() / 2)));
		ships = network.getAllShips();
		myShip = network.getMyShip();
		opponent = network.getOpponent();
		opponent.isEnemy = true;
		bullets = new ArrayList<Bullet>();
		opponentThread = new NetworkUpdateThread(network, opponent, bullets);
		ships.add(myShip);
		scoreboard = new Scoreboard();
		canvas.init(ships, bullets, scoreboard);
		mouseLocation = new Point();
		gamePaused = true;
		this.setOnMouseMoved(MouseMoved());
		this.setOnMousePressed(MousePressed());
		this.setOnKeyPressed(KeyPressed());
		this.setOnKeyReleased(KeyReleased());
	}
	
	public void start() {
		opponentThread.start();
		BulletCounter.setTeam(myShip.getId());
		canvas.addMessage(new Message("SYNCHRONIZING...", 4, Color.GRAY));
		startRound(1);
		
		new AnimationTimer() {
			public void handle(long currentNanoTime) {
				// If a message is displayed on screen, game is paused
				gamePaused = canvas.messageDisplayed();
				update();
			}
		}.start();
	}
	
	private void startRound(int round) {
		// reset positions
		canvas.addMessage(new Message("ROUND " + round, 2, Color.WHITE));
		canvas.addMessage(new Message("WIN OR DIE", 0.25, Color.WHITE));
		scoreboard.reset();
		scoreboard.start();
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
			Bullet bullet = opponent.createEnemyBullet();
			if(bullet != null) {
				bullets.add(bullet);
			}
		}
		
		// Iterate through bullets and update them
		for(int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			// Collision checking
			if(b.getPlayer() != myShip.getId()) {
				if(MathStuffs.isCollision(b, myShip)) {
					myShip.takeDamage(b.getDamage());
					myShip.hitBy = b.getId();
					System.out.println(myShip.getHealth());
					if(myShip.getHealth() <= 0) {
						startRound(2);
					}
					bullets.remove(b);
				}
			}
			if(!b.step()) {
				bullets.remove(b);
			}
		}
		System.out.print("{");
		for(Bullet b : bullets) {
			System.out.print(b.getId() + ", ");
		}
		System.out.println("}");
		canvas.repaint();
		network.sendShipState(myShip);
		myShip.isFiring = false;
		myShip.hitBy = -1;
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