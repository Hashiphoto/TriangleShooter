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
import levels.Corridor;
import levels.FourSquare;
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
	private ArrayList<Level> levels;
	private Ship myShip;
	private Ship opponent;
	private Network network;
	private NetworkUpdateThread opponentThread;
	private Point mouseLocation;
	private Scoreboard scoreboard;
	private int currentRound;
	
	private enum gameState {
		PLAYING, PAUSED, WAITING_FOR_LEVEL, WAITING_FOR_START
	};
	private static byte GAME_START = 0;
	private gameState state;
	
	public GameScene(Network network, Group group, GameCanvas canvas) {
		super(group);
		this.network = network;
		this.canvas = canvas;
		network.initializeShips(new Point(200, (int) (canvas.getHeight() + GameCanvas.HUD_HEIGHT) / 2),
								new Point((int) canvas.getWidth() - 200, (int) (canvas.getHeight() + GameCanvas.HUD_HEIGHT)/ 2));
		ships = network.getAllShips();
		myShip = network.getMyShip();
		opponent = network.getOpponent();
		opponent.isEnemy = true;
		bullets = new ArrayList<Bullet>();
		opponentThread = new NetworkUpdateThread(network, opponent, bullets);
		ships.add(myShip);
		scoreboard = new Scoreboard();
		mouseLocation = new Point();
		currentRound = 1;
		walls = new ArrayList<Wall>();
		initializeLevels();
		this.setOnMouseMoved(MouseMoved());
		this.setOnMousePressed(MousePressed());
		this.setOnKeyPressed(KeyPressed());
		this.setOnKeyReleased(KeyReleased());
	}
	
	public void start() {
		opponentThread.start();
		BulletCounter.setTeam(myShip.getId());
		canvas.addMessage(new Message("SYNCHRONIZING...", 2, Color.GRAY));
		setupNewLevel();
		canvas.init(ships, bullets, scoreboard, walls);
		new AnimationTimer() {
			public void handle(long currentNanoTime) {
				// If a message is displayed on screen, game is paused
				if(state == gameState.PLAYING && canvas.messageDisplayed()) {
					state = gameState.PAUSED;
				}
				update();
			}
		}.start();
	}
	
	private void declareWinner(int roundWinner) {
		if(roundWinner != -1) {
			String winner = Ship.Name[roundWinner];
			canvas.addMessage(new Message(winner + " WINS!", 2, GameCanvas.ShipColors[roundWinner]));
			scoreboard.win(roundWinner);
		}
	}
	
	private void setupNewLevel() {
		if(network.isHosting()) {
			state = gameState.WAITING_FOR_START;
			int index = getRandomLevel();
			setLevel(index);
			//Send level only
			network.sendGameInformation((byte) index, (byte) -1);
			System.out.println("Sent level");
		}
		else {
			state = gameState.WAITING_FOR_LEVEL;
		}
	}
	
	private void startRound() {
		state = gameState.PAUSED;
		myShip.reset();
		canvas.addMessage(new Message("ROUND " + currentRound, 2, Color.WHITE));
		canvas.addMessage(new Message("WIN OR DIE", 0.25, Color.WHITE));
		scoreboard.start();
		currentRound++;
		state = gameState.PLAYING;
	}
	
	private void update() {
//		System.out.println(state);
		switch(state) {
		case PLAYING:
		case PAUSED:
			playStep();
			break;
		case WAITING_FOR_LEVEL:
			if(opponentThread.hasNewLevel()) {
				System.out.println("Recieved level");
				setLevel(opponentThread.getLevel());
				// Got the level, start the game
				delay(1.0, e -> network.sendGameInformation((byte) -1, GAME_START));
				System.out.println("Sent game start");
			}
			break;

		case WAITING_FOR_START:
			playStep();
			if(opponentThread.hasFreshAction()) {
				System.out.println("Recieved game start");
				doAction(opponentThread.getAction());
			}
			break;
		}
		
		canvas.repaint();
		network.sendShipState(myShip);
		myShip.isFiring = false;
		myShip.hitBy = -1;
	}
	
	private void doAction(int action) {
		if(action == 0) {
			startRound();
		}
	}
	
	private void playStep() {
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
		if(state == gameState.PLAYING) {
			int winner = getWinner();
			if(winner != -1) {
				declareWinner(winner);
			}
		}
		else {
			myShip.stop();
			opponent.stop();
		}
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
	
	public void initializeLevels() {
		levels = new ArrayList<Level>();
		levels.add(new Corridor());
		levels.add(new FourSquare());
		levels.add(new Level());
	}
	
	public int getRandomLevel() {
		int random = (int) (Math.random() * levels.size());
		return random;
	}
	
	public void setLevel(int index) {
		walls.clear();
		walls.addAll(levels.get(index).getWalls());
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
				if(state == gameState.PAUSED) {
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
				if(state == gameState.PAUSED) {
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
				if(state == gameState.PAUSED) {
					return;
				}
				String code = event.getCode().toString();
				myShip.keyReleased(code);
			}
		};
	}
}