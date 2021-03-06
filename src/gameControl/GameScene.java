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
import levels.Divide;
import levels.Dot;
import levels.FourSquare;
import levels.Level;
import network.Network;
import network.NetworkUpdateThread;

/**
 * This extends JavaFX's Scene and is the main component of the game. It uses AnimationTimer for the
 * pace and redrawing speed of the game. start() must be called once at the beginning to start the game.
 * @author Trent
 *
 */
public class GameScene extends Scene {
	private static final int ROUNDS = 7;
	private static final byte NO_BYTE = -1;
	private static final int SPEED_REWARD = 1;
	private static final double ACCEL_REWARD = 0.05;
	private static final double ROTATION_REWARD = -1.7;
	private static final double ACCURACY_REWARD = -0.05;
	private static final int RANGE_REWARD = 50;
	private static final int DAMAGE_REWARD = 1;
	private static final int RELOAD_REWARD = -60;
	private static final int BSPEED_REWARD = 2;
	private static final int CLIP_REWARD = 1;
	private static final int BSIZE_REWARD = 4;
	private final double statStolen = 1.0;
	private final double winBonus = 2.0;
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
		PLAYING, PAUSED, WAITING_FOR_LEVEL, WAITING_FOR_START, WAITING_FOR_UPGRADE
	};
	private final byte GAME_START = 0;
	private final byte LOSE = 1;
	private gameState state;
	private PowerMeterPanel pmp;
	private int lastWinner;

	/**
	 * Instantiate a new GameScene
	 * @param network	This must be connected to another client already either has host or guest
	 * @param group		The JavaFX Group that encloses this scene
	 * @param canvas	The canvas that will be used to draw the components
	 */
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
		initializeMeters();
		lastWinner = -1;
		initializeLevels();
		this.setOnMouseMoved(MouseMoved());
		this.setOnMousePressed(MousePressed());
		this.setOnKeyPressed(KeyPressed());
		this.setOnKeyReleased(KeyReleased());
	}
	
	/**
	 * Begin the game. This can only be called once. The game will automatically reset after
	 * someone has won.
	 */
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
	
	/**
	 * Reset the game state
	 */
	private void hardReset() {
		myShip.hardReset();
		myShip.reset();
		opponent.hardReset();
		opponent.reset();
		bullets.clear();
		scoreboard.reset();
		currentRound = 0;
		initializeMeters();

		if(network.isHosting()) {
			setupNewLevel();
		}
		else {
			state = gameState.WAITING_FOR_LEVEL;
		}
	}
	
	/**
	 * Set all the PowerMeter's ranges and initialize them to the Ship default value
	 */
	private void initializeMeters() {
		for(int i = 0; i < pmp.meters.size(); i++) {
			PowerMeter meter = pmp.meters.get(i);
			int roundsToWin =  ROUNDS / 2;
			double baseValue = 0;
			double rewardIncrement = 0;
			switch(i) {
			case 0: // Movement
				baseValue = Ship.DEFAULT_SHIP_MAX_SPEED;
				rewardIncrement = SPEED_REWARD;
				break;
			case 1: // Accuracy
				meter.isBackwards = true;
				baseValue = Ship.DEFAULT_ACCURACY;
				rewardIncrement = ACCURACY_REWARD;
				meter.setLimits(baseValue + -rewardIncrement * statStolen * roundsToWin, 0);
				meter.setVal0(baseValue);
				meter.setVal1(baseValue);
				continue;
			case 2: // Sniping
				baseValue = Ship.DEFAULT_BULLET_RANGE;
				rewardIncrement = RANGE_REWARD;
				break;
			case 3: // Bullet Size
				baseValue = Ship.DEFAULT_BULLET_SIZE;
				rewardIncrement = BSIZE_REWARD;
				break;
			case 4: // Ammo
				baseValue = Ship.DEFAULT_CLIP_SIZE;
				rewardIncrement = CLIP_REWARD;
				break;
			case 5: // Reload
				meter.isBackwards = true;
				baseValue = Ship.DEFAULT_RELOAD_TIME;
				rewardIncrement = RELOAD_REWARD;
				break;
			}
			meter.setVal0(baseValue);
			meter.setVal1(baseValue);
			// You can only lose three rounds
			meter.setLimits(baseValue - (rewardIncrement * statStolen * roundsToWin), 
					// You can upgrade by winning three times and by losing three times
					baseValue + (rewardIncrement * winBonus * roundsToWin) + (rewardIncrement * statStolen * roundsToWin));

		}
	}
	
	/**
	 * Show who won the previous round and start the upgrade screen 
	 * @param roundWinner	The id of who won the previous round
	 */
	private void declareWinner(int roundWinner) {
		String winner = Ship.Name[roundWinner];
		lastWinner = roundWinner;
		canvas.addMessage(new Message(winner + " WINS!", 1.5, GameCanvas.ShipColors[roundWinner]));
		scoreboard.win(roundWinner);
		if(myShip.getId() == roundWinner) {
			pmp.setDisabled(true);
			canvas.addMessage(new Message("Wait for opponent to choose", 1.5, GameCanvas.NEUTRAL));
		}
		else {
			pmp.setDisabled(false);
			canvas.addMessage(new Message("Steal a stat", 1.5, GameCanvas.NEUTRAL));
		}
		pmp.enableAllMeters();
		delay(3.0, e -> pmp.visible = true);
	}
	
	/**
	 * Show the game winner and reset everything
	 * @param roundWinner	The id of who won the gme
	 */
	private void concludeGame(int roundWinner) {
		String winner = Ship.Name[roundWinner];
		canvas.addMessage(new Message(winner + " WINS IT ALL!!", 4, GameCanvas.ShipColors[roundWinner]));
		scoreboard.win(roundWinner);
		delay(4, e -> hardReset());
	}
	
	/**
	 * Create a new level and send it to the opponent. They will send back the Start game signal when it is received
	 */
	private void setupNewLevel() {
		state = gameState.WAITING_FOR_START;
		int index = getRandomLevel();
		setLevel(index);
		//Send level only
		network.sendGameInformation((byte) index, NO_BYTE, NO_BYTE);
	}
	
	/**
	 * Start the new round after a short delay
	 */
	private void startRound() {
		state = gameState.PAUSED;
		delay(2.0, e -> state = gameState.PLAYING);
		pmp.visible = false;
		myShip.reset();
		bullets.clear();
		BulletCounter.reset();
		canvas.addMessage(new Message("ROUND " + currentRound, 2, Color.WHITE));
		canvas.addMessage(new Message("WIN OR DIE", 0.25, Color.WHITE));
		scoreboard.start();
		currentRound++;
	}
	
	/**
	 * This is run every frame to figure out what to do based on the Game State
	 */
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
		case WAITING_FOR_UPGRADE:
			updateShip();
			myShip.stop();
			opponent.stop();
			checkForUpgrade();
		}
		
		canvas.repaint();
		network.sendShipState(myShip);
		myShip.isFiring = false;
		myShip.hitBy = -1;
	}
	
	/**
	 * Perform a predefined action sent by the opponent
	 * @param action	The id of the action to perform
	 */
	private void doAction(int action) {
		switch(action) {
		case GAME_START: 
			startRound();
			break;
		case LOSE: 
			win();
			break;
		}
	}
	
	/**
	 * Perform one game tick on each ship object
	 */
	private void updateShip() {
		myShip.step(mouseLocation);
		opponent.checkReload();
	}
	
	/**
	 * This is run every frame during the match to determine collisions, bullet
	 * firing, and round endings
	 */
	private void playStep() {
		updateShip();
		if(myShip.isFiring || myShip.burstFiring) {
			Bullet bullet = myShip.createBullet();
			if(bullet != null) {
				bullets.add(bullet);
			}
			else {
				myShip.isFiring = false;
				myShip.burstFiring = false;
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
	
	/**
	 * Run when the player has won a round. It will evaluate if there is a game winner as well
	 */
	private void win() {
		state = gameState.WAITING_FOR_UPGRADE;
		if(scoreboard.getWins(myShip.getId()) == ROUNDS / 2) {
			concludeGame(myShip.getId());
		}
		else {
			declareWinner(myShip.getId());
		}
	}
	
	/**
	 * Run when the player has lost a round. It will evaluate if there is a game winner as well
	 */
	private void lose() {
		state = gameState.PAUSED;
		network.sendGameInformation(NO_BYTE, LOSE, NO_BYTE);
		if(scoreboard.getWins(opponent.getId()) == ROUNDS / 2) {
			concludeGame(opponent.getId());
		}
		else {
			declareWinner(opponent.getId());
		}
	}
	
	/**
	 * See if the opponent has selected a level to play on yet. When it is received, send the game start signal
	 */
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
	
	/**
	 * Check if any actions have been sent by the opponent and execute them
	 * @return	True if there was at least one pending action
	 */
	private boolean checkForAction() {
		if(opponentThread.hasFreshAction()) {
			doAction(opponentThread.getAction());
			return true;
		}
		return false;
	}
	
	/**
	 * Check if the opponent has selected an upgrade and recreate the settings locally
	 */
	private void checkForUpgrade() {
		if(opponentThread.hasFreshUpgrade()) {
			UpdateShipPower(opponentThread.getUpgrade(), true);
			if(lastWinner == myShip.getId()) {
				canvas.addMessage(new Message("Upgrade a stat (x2)", 1.5, GameCanvas.NEUTRAL));
				pmp.setDisabled(false);
				state = gameState.PAUSED;
			}
			else {
				state = gameState.WAITING_FOR_LEVEL;
			}
		}
	}
	/**
	 * Change the attributes of both local copies of the ships
	 * @param upgrade	The ID of the upgrade selected
	 * @param received	True if this was called from the opponent. False if it was called locally
	 */
	private void UpdateShipPower(int upgrade, boolean received) {
		double winnerModifier = -statStolen;
		double loserModifier = statStolen;
		// If we won and are picking our upgrade OR we lost and they are picking their upgrade
		if((lastWinner == myShip.getId()) == (received == false)) {
			winnerModifier = winBonus;
			loserModifier = 0.0;
		}
		Ship winner, loser, player0, player1;
		if(lastWinner == myShip.getId()){
			winner = myShip;
			loser = opponent;
		}
		else {
			winner = opponent;
			loser = myShip;
		}
		if(myShip.getId() == 0) {
			player0 = myShip;
			player1 = opponent;
		}
		else {
			player0 = opponent;
			player1 = myShip;
		}
		
		switch(upgrade) {
		case 0: // Movement
			winner.setMaxSpeed((int) (winner.getMaxSpeed() + winnerModifier * SPEED_REWARD));
			winner.setAcceleration(winner.getAcceleration() + winnerModifier * ACCEL_REWARD);
			winner.setRotationSpeed(winner.getRotationSpeed() + winnerModifier * ROTATION_REWARD);
			loser.setMaxSpeed((int) (loser.getMaxSpeed() + loserModifier * SPEED_REWARD));
			loser.setAcceleration(loser.getAcceleration() + loserModifier * ACCEL_REWARD);
			loser.setRotationSpeed(loser.getRotationSpeed() + loserModifier * ROTATION_REWARD);
			pmp.meters.get(0).setVal0(player0.getMaxSpeed());
			pmp.meters.get(0).setVal1(player1.getMaxSpeed());
			if(received) { pmp.meters.get(0).disabled = true; }
			break;
		case 1: // Accuracy
			winner.setAccuracy(winner.getAccuracy() + winnerModifier * ACCURACY_REWARD);
			loser.setAccuracy(loser.getAccuracy() + loserModifier * ACCURACY_REWARD);
			pmp.meters.get(1).setVal0(player0.getAccuracy());
			pmp.meters.get(1).setVal1(player1.getAccuracy());
			if(received) { pmp.meters.get(1).disabled = true; }
			break;
		case 2: // Sniping
			winner.setBulletRange((int) (winner.getBulletRange() + winnerModifier * RANGE_REWARD));
			winner.setBulletSpeed((int) (winner.getBulletSpeed() + winnerModifier * BSPEED_REWARD));
			loser.setBulletRange((int) (loser.getBulletRange() + loserModifier * RANGE_REWARD));
			loser.setBulletSpeed((int) (loser.getBulletSpeed() + loserModifier * BSPEED_REWARD));
			pmp.meters.get(2).setVal0(player0.getBulletRange());
			pmp.meters.get(2).setVal1(player1.getBulletRange());
			if(received) { pmp.meters.get(2).disabled = true; }
			break;
		case 3: // Bullet Size
			winner.setBulletSize((int) (winner.getBulletSize() + winnerModifier * BSIZE_REWARD));
			winner.setDamage((int) (winner.getDamage() + winnerModifier * DAMAGE_REWARD));
			loser.setBulletSize((int) (loser.getBulletSize() + loserModifier * BSIZE_REWARD)); 
			loser.setDamage((int) (loser.getDamage() + loserModifier * DAMAGE_REWARD));
			pmp.meters.get(3).setVal0(player0.getBulletSize());
			pmp.meters.get(3).setVal1(player1.getBulletSize());
			if(received) { pmp.meters.get(3).disabled = true; }
			break;
		case 4: // Ammo
			winner.setClipSize((int) (winner.getClipSize() + winnerModifier * CLIP_REWARD));
			loser.setClipSize((int) (loser.getClipSize() + loserModifier * CLIP_REWARD));
			pmp.meters.get(4).setVal0(player0.getClipSize());
			pmp.meters.get(4).setVal1(player1.getClipSize());
			if(received) { pmp.meters.get(4).disabled = true; }
			break;
		case 5: // Reload
			winner.setReloadTime((int) (winner.getReloadTime() + winnerModifier * RELOAD_REWARD));
			loser.setReloadTime((int) (loser.getReloadTime() + loserModifier * RELOAD_REWARD));
			pmp.meters.get(5).setVal0(player0.getReloadTime());
			pmp.meters.get(5).setVal1(player1.getReloadTime());
			if(received) { pmp.meters.get(5).disabled = true; }
			break;
		}
	}
	
	/**
	 * Fill the Level ArrayList with one copy of each level
	 */
	private void initializeLevels() {
		levels = new ArrayList<Level>();
		levels.add(new Corridor());
		levels.add(new FourSquare());
		levels.add(new Level());
		levels.add(new Divide());
		levels.add(new Dot());
	}
	
	/**
	 * Picks a random level in the range of the level ArrayList
	 * @return	The id of the level selected
	 */
	private int getRandomLevel() {
		int random = (int) (Math.random() * levels.size());
		return random;
	}
	
	/**
	 * Set the walls of the room to be the given level's
	 * @param index	The id of the selected room
	 */
	private void setLevel(int index) {
		walls.clear();
		walls.addAll(levels.get(index).getWalls());
	}
	
	/**
	 * Run an action after a specified delay. This uses Timeline so that it is compatible with the Animation
	 * Timer that is constantly running
	 * @param duration	The time in seconds to wait before executing the action
	 * @param event		The action to perform
	 */
	private void delay(double duration, EventHandler<ActionEvent> event) {
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(duration), event));
	    timeline.setCycleCount(1);
	    timeline.play();
	}
	
	/**
	 * Set a binding in the containing stage so that this occurs when the stage loses focus
	 */
	public void onLostFocus() {
		myShip.releaseKeys();
	}
	
	/**
	 * Binding to track mouse movement
	 * @return	The MouseMovement EventHandler
	 */
	private EventHandler<MouseEvent> MouseMoved() {
		return new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				mouseLocation.x = (int) event.getX();
				mouseLocation.y = (int) event.getY();
			}
		};
	}
	
	/**
	 * Binding to track mouse clicks. It also detects which upgrade was clicked on
	 * @return	The MousePressed EventHandler
	 */
	private EventHandler<MouseEvent> MousePressed() {
		return new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(state != gameState.PLAYING) {
					// If in selection mode
					if(pmp.visible && !pmp.isDisabled()) {
						byte buttonPressed = (byte) canvas.getMeterButtonPressed(event.getX(), event.getY());
						// Click on a button
						if(buttonPressed != -1) {
							pmp.setDisabled(true);
							UpdateShipPower(buttonPressed, false);
							network.sendGameInformation(NO_BYTE, NO_BYTE, buttonPressed);
							if(lastWinner == myShip.getId()) {
								setupNewLevel();
							}
							else {
								state = gameState.WAITING_FOR_UPGRADE;
							}
						}
					}
					return;
				}
				if(event.getButton() == MouseButton.PRIMARY) {
					myShip.isFiring = true;
					return;
				}
				if(event.getButton() == MouseButton.SECONDARY) {
					myShip.burstFiring = true;
				}
			}
		};
	}
	
	/**
	 * EventHandler to handle key presses and WASD movement
	 * @return	The KeyPressed EventHandler
	 */
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
				else if(code == "E") {
					myShip.burstFiring = true;
				}
			}
		};
	}
	
	/**
	 * EventHandler for key releases and WASD movement
	 * @return	The KeyReleased EventHandler
	 */
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