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
	private static byte LOSE = 1;
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
		if(network.isHosting()) {
			state = gameState.WAITING_FOR_START;
		}
		else {
			System.out.println("Waiting for level");
			state = gameState.WAITING_FOR_LEVEL;
		}
		opponentThread.start();
		BulletCounter.setTeam(myShip.getId());
		canvas.addMessage(new Message("SYNCHRONIZING...", 1, Color.GRAY));
		canvas.init(ships, bullets, scoreboard, walls);
		new AnimationTimer() {
			public void handle(long currentNanoTime) {
				update();
			}
		}.start();
		
		if(network.isHosting()) {
			setupNewLevel();
		}
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
			int index = getRandomLevel();
			setLevel(index);
			//Send level only
			network.sendGameInformation((byte) index, (byte) -1);
			System.out.println("Sent level");
		}
	}
	
	private void startRound() {
		state = gameState.PAUSED;
		delay(2.0, e -> state = gameState.PLAYING);
		myShip.reset();
		canvas.addMessage(new Message("ROUND " + currentRound, 2, Color.WHITE));
		canvas.addMessage(new Message("WIN OR DIE", 0.25, Color.WHITE));
		scoreboard.start();
		currentRound++;
	}
	
	private void update() {
		System.out.println(state);
		switch(state) {
		case PLAYING:
			playStep();
			break;
		case PAUSED:
			myShip.stop();
			opponent.stop();
			break;
		case WAITING_FOR_LEVEL:
			checkForLevel();
			break;

		case WAITING_FOR_START:
			checkForStart();
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
		if (action == 1) {
			win();
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
		
		// Check to see if I've lost
		if(opponentThread.hasFreshAction()) {
			doAction(opponentThread.getAction());
		}
		else if(myShip.getHealth() <= 0) {
			lose();
		}
	}
	
	private void win() {
		state = gameState.PAUSED;
		declareWinner(myShip.getId());
	}
	
	private void lose() {
		state = gameState.PAUSED;
		network.sendGameInformation((byte) -1, LOSE);
		declareWinner(opponent.getId());
	}
	
	private void checkForLevel() {
		if(opponentThread.hasNewLevel()) {
			System.out.println("Recieved level");
			setLevel(opponentThread.getLevel());
			// Got the level, start the game
			delay(0.5, e -> {
				network.sendGameInformation((byte) -1, GAME_START);
				startRound();
			});
			System.out.println("Sent game start");
		}
	}
	
	private void checkForStart() {
		if(opponentThread.hasFreshAction()) {
			System.out.println("Recieved game start");
			doAction(opponentThread.getAction());
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
				if(state != gameState.PLAYING) {
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
				if(state != gameState.PLAYING) {
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
				if(state != gameState.PLAYING) {
					return;
				}
				String code = event.getCode().toString();
				myShip.keyReleased(code);
			}
		};
	}
}