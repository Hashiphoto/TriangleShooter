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
	private static final int ROUNDS = 7;
	private static final byte NO_BYTE = -1;
	
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
		PLAYING, PAUSED, WAITING_FOR_LEVEL, WAITING_FOR_START, WAITING_FOR_LOSER
	};
	private static byte GAME_START = 0;
	private static byte LOSE = 1;
	private gameState state;
	private PowerMeterPanel pmp;
	private int lastWinner;
	
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
		pmp = new PowerMeterPanel();
		lastWinner = -1;
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
			state = gameState.WAITING_FOR_LEVEL;
		}
		opponentThread.start();
		BulletCounter.setTeam(myShip.getId());
		canvas.addMessage(new Message("Synchronizing...", 1, Color.GRAY));
		canvas.init(ships, bullets, scoreboard, walls, pmp);
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
		String winner = Ship.Name[roundWinner];
		lastWinner = roundWinner;
		canvas.addMessage(new Message(winner + " WINS!", 1.5, GameCanvas.ShipColors[roundWinner]));
		scoreboard.win(roundWinner);
		if(myShip.getId() == roundWinner) {
			pmp.disabled = true;
			canvas.addMessage(new Message("Wait for opponent to choose", 1.5, GameCanvas.NEUTRAL));
		}
		else {
			pmp.disabled = false;
			canvas.addMessage(new Message("Steal a stat", 1.5, GameCanvas.NEUTRAL));
		}
		delay(3.0, e -> pmp.visible = true);
	}
	
	private void setupNewLevel() {
		state = gameState.WAITING_FOR_START;
		int index = getRandomLevel();
		setLevel(index);
		//Send level only
		network.sendGameInformation((byte) index, NO_BYTE, NO_BYTE);
	}
	
	private void startRound() {
		state = gameState.PAUSED;
		delay(2.0, e -> state = gameState.PLAYING);
		pmp.visible = false;
		myShip.reset();
		bullets.clear();
		canvas.addMessage(new Message("ROUND " + currentRound, 2, Color.WHITE));
		canvas.addMessage(new Message("WIN OR DIE", 0.25, Color.WHITE));
		scoreboard.start();
		currentRound++;
	}
	
	private void update() {
//		System.out.println(state);
		switch(state) {
		case PLAYING:
			playStep();
			break;
		case PAUSED:
			updateShip();
			myShip.stop();
			opponent.stop();
			break;
		case WAITING_FOR_LEVEL:
			updateShip();
			checkForLevel();
			break;
		case WAITING_FOR_START:
			updateShip();
			checkForAction();
			break;
		case WAITING_FOR_LOSER:
			if(checkForUpgrade()) {
				pmp.disabled = false;
				state = gameState.PAUSED;
			}
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
	
	private void updateShip() {
		myShip.step(mouseLocation);
	}
	
	private void playStep() {
		updateShip();
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
		
		// Check to see if opponent lost
		if(!checkForAction()) {
			// Check to see if I lost
			if(myShip.isDead()) {
				lose();
			}
		}
	}
	
	private void win() {
		state = gameState.WAITING_FOR_LOSER;
		declareWinner(myShip.getId());
	}
	
	private void lose() {
		state = gameState.PAUSED;
		network.sendGameInformation(NO_BYTE, LOSE, NO_BYTE);
		declareWinner(opponent.getId());
	}
	
	private void checkForLevel() {
		if(opponentThread.hasNewLevel()) {
			setLevel(opponentThread.getLevel());
			// Got the level, start the game
			delay(1.5, e -> {
				network.sendGameInformation(NO_BYTE, GAME_START, NO_BYTE);
				startRound();
			});
		}
	}
	
	private boolean checkForAction() {
		if(opponentThread.hasFreshAction()) {
			doAction(opponentThread.getAction());
			return true;
		}
		return false;
	}
	
	private boolean checkForUpgrade() {
		if(opponentThread.hasFreshUpgrade()) {
			UpdateShipPower(opponentThread.getUpgrade());
			return true;
		}
		return false;
	}
	
	private void UpdateShipPower(int upgrade) {
		switch(upgrade) {
		case 0:
			System.out.println("Upgrading speed");
			break;
		case 1:
			System.out.println("Upgrading Accuracy");
			break;
		case 2:
			System.out.println("Upgrading range");
			break;
		case 3:
			System.out.println("Upgrading dmg");
			break;
		case 4:
			System.out.println("Upgrading size");
			break;
		case 5:
			System.out.println("Upgrading reload");
			break;
		}
	}
	
	private void initializeLevels() {
		levels = new ArrayList<Level>();
		levels.add(new Corridor());
		levels.add(new FourSquare());
		levels.add(new Level());
	}
	
	private int getRandomLevel() {
		int random = (int) (Math.random() * levels.size());
		return random;
	}
	
	private void setLevel(int index) {
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
	
	private EventHandler<MouseEvent> MouseMoved() {
		return new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mouseLocation.x = (int) event.getX();
				mouseLocation.y = (int) event.getY();
			}
		};
	}
	
	private EventHandler<MouseEvent> MousePressed() {
		return new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(state != gameState.PLAYING) {
					if(pmp.visible && !pmp.disabled) {
						byte buttonPressed = (byte) canvas.getMeterButtonPressed(event.getX(), event.getY());
						if(buttonPressed != -1) {
							if(lastWinner == myShip.getId()) {
								setupNewLevel();
							}
							else {
								pmp.disabled = true;
								state = gameState.WAITING_FOR_LEVEL;
								network.sendGameInformation(NO_BYTE, NO_BYTE, buttonPressed);
							}
						}
					}
					return;
				}
				if(event.getButton() == MouseButton.PRIMARY) {
					myShip.isFiring = true;
				}
			}
		};
	}
	
	private EventHandler<KeyEvent> KeyPressed() {
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
	
	private EventHandler<KeyEvent> KeyReleased() {
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