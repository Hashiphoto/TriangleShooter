package gameControl;

import java.awt.Point;
import java.util.ArrayList;

import gameElements.Bullet;
import gameElements.BulletCounter;
import gameElements.Ship;
import gameElements.Wall;
import gui.GameCanvas;
import gui.Scoreboard;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import levels.Level;
import network.Network;
import network.NetworkUpdateThread;

public class GameScene extends Scene {
	public static final int FRAMERATE = 60;
	private static final int ROUNDS = 7;
	
	private GameCanvas canvas;
	private ArrayList<Ship> ships;
	private ArrayList<Bullet> bullets;
	private ArrayList<Wall> walls;
	private Ship myShip;
	private Ship opponent;
	private Network network;
	private NetworkUpdateThread opponentThread;
	private Point mouseLocation;
	private boolean gamePaused;
	private Scoreboard scoreboard;
	private int currentRound;
	
	public GameScene(Network network, Group group, GameCanvas canvas) {
		super(group);
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
		mouseLocation = new Point();
		gamePaused = true;
		currentRound = 1;
		walls = new Level().getWalls();
		canvas.init(ships, bullets, scoreboard, walls);
		this.setOnMouseMoved(MouseMoved());
		this.setOnMousePressed(MousePressed());
		this.setOnKeyPressed(KeyPressed());
		this.setOnKeyReleased(KeyReleased());
	}
	
	public void start() {
		opponentThread.start();
		BulletCounter.setTeam(myShip.getId());
		canvas.addMessage(new Message("SYNCHRONIZING...", 1, Color.GRAY));
		startNextRound(-1);
		
		new AnimationTimer() {
			public void handle(long currentNanoTime) {
				// If a message is displayed on screen, game is paused
				gamePaused = canvas.messageDisplayed();
				update();
			}
		}.start();
	}
	
	private void startNextRound(int roundWinner) {
		if(roundWinner != -1) {
			String winner = Ship.Name[roundWinner];
			canvas.addMessage(new Message(winner + " WINS!", 2, GameCanvas.ShipColors[roundWinner]));
			scoreboard.win(roundWinner);
		}
		delay(2, e -> myShip.reset());
		canvas.addMessage(new Message("ROUND " + currentRound, 2, Color.WHITE));
		canvas.addMessage(new Message("WIN OR DIE", 0.25, Color.WHITE));
		scoreboard.start();
		currentRound++;
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

		myShip.setLocation(MathStuffs.collide(myShip.getLocation(), walls));
		// Iterate through bullets and update them
		for(int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			// Collision checking
			if(b.getPlayer() != myShip.getId()) {
				if(MathStuffs.isCollision(b, myShip)) {
					myShip.takeDamage(b.getDamage());
					myShip.hitBy = b.getId();
					bullets.remove(b);
				}
			}
			if(!b.step()) {
				bullets.remove(b);
			}
		}
		if(!gamePaused) {
			int winner = getWinner();
			if(winner != -1) {
				startNextRound(winner);
			}
		}
		else {
			myShip.stop();
			opponent.stop();
		}
		canvas.repaint();
		network.sendShipState(myShip);
		myShip.isFiring = false;
		myShip.hitBy = -1;
	}
	
	private int getWinner() {
		if(myShip.getHealth() <= 0) {
			return opponent.getId();
		}
		if(opponent.getHealth() <= 0) {
			return myShip.getId();
		}
		return -1;
	}
	
	private Ship getShipById(int id) {
		for(Ship s : ships) {
			if(s.getId() == id) {
				return s;
			}
		}
		return null;
	}
	
	private void delay(double duration, EventHandler<ActionEvent> event) {
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(duration), event));
	    timeline.setCycleCount(1);
	    timeline.play();
	}
	
	public void onLostFocus() {
		myShip.releaseKeys();
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
				myShip.keyPressed(code);
				if(code == "SPACE") {
					myShip.isFiring = true;	
				}
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
				myShip.keyReleased(code);
			}
		};
	}
}