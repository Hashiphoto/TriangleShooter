package gui;

import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import gameControl.MathStuffs;
import gameControl.Message;
import gameElements.Bullet;
import gameElements.Ship;
import gameElements.Wall;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class GameCanvas extends Canvas {
	public static final Color[] ShipColors = {
		Color.CYAN,
		Color.ORANGE
	};
	private static final Color NEUTRAL = Color.WHITE;
	private static final int SHIP_SIDE_LENGTH = 22;
	private static final int SHIP_FRONT_LENGTH = 40;
	private static final int SHIP_OVAL_SIZE = 15;
	private static final int BULLET_SIZE = 6;
	public static final int HUD_HEIGHT = 60;
	private static final int CLOCK_WIDTH = 50;
	private static final int HUD_BORDER = 20;
	private static final int HEALTH_WIDTH = 450;
	private static final int MED_BORDER = 35;
	private static final int WIN_SIZE = 20;
	private static final int INNER_WIN_SIZE = 16;
	private static final int WIN_BORDER = 4;
	private static final int INNER_WIN_BORDER = 2;
	private static final Font AGENCY_LARGE = new Font("Agency FB", 100);
	private static final Font AGENCY_CLOCK = new Font("Agency FB", 40);
	private ArrayList<Ship> ships;
	private ArrayList<Bullet> bullets;
	private ArrayList<Message> messages;
	private ArrayList<Wall> walls;
	private int numShips;
	private GraphicsContext gc;
	private Scoreboard scoreboard;
	private int lastTimeSeconds;
	
	public GameCanvas(int width, int height) {
		super(width, height);
		gc = this.getGraphicsContext2D();
		messages = new ArrayList<Message>();
	}

	public void init(ArrayList<Ship> ships, ArrayList<Bullet> bullets, Scoreboard scoreboard, ArrayList<Wall> walls) {
		this.ships = ships;
		this.bullets = bullets;
		this.numShips = ships.size();
		this.scoreboard = scoreboard;
		this.lastTimeSeconds = 0;
		this.walls = walls;
	}
	
	public void repaint() {
		gc.setFill(Color.BLACK);
		gc.fillRect(0, HUD_HEIGHT, this.getWidth(), this.getHeight());
		drawShips();
		drawBullets();
		drawMessage();
		drawScoreboard();
		drawWalls();
	}
	
	public void addMessage(Message msg) {
		messages.add(msg);
	}
	
	public boolean messageDisplayed() {
		return !messages.isEmpty();
	}
	
	public void drawWalls() {
		int numWalls = walls.size();
		gc.setStroke(NEUTRAL);
		for(int i = 0; i < numWalls; i++) {
			Wall wall = walls.get(i);
			gc.strokeRect(wall.x1, wall.y1, wall.width(), wall.height());
		}
	}
	
	private void drawScoreboard() {
		if(scoreboard.getGameTime() != lastTimeSeconds) {
			gc.setFill(Color.web("0x333333"));
			
			gc.fillRect(0, 0, this.getWidth(), HUD_HEIGHT);
			gc.setStroke(NEUTRAL);
			gc.strokeRect((this.getWidth() - CLOCK_WIDTH) / 2, 0, CLOCK_WIDTH, HUD_HEIGHT);
			drawGameTime(scoreboard.getGameTime());
			drawHealthBars();
			drawWins();
		}
	}
	
	private void drawGameTime(int time) {
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setFont(AGENCY_CLOCK);
		gc.setFill(NEUTRAL);
		gc.fillText(Integer.toString(time), this.getWidth() / 2, 45);
	}
	
	private void drawHealthBars() {
		double maxHealth = ships.get(0).getMaxHealth();
		gc.setStroke(ShipColors[0]);
		gc.setFill(ShipColors[0]);
		gc.strokeRect(HUD_BORDER, HUD_BORDER, HEALTH_WIDTH, HUD_HEIGHT - 2 * HUD_BORDER);
		gc.fillRect(HUD_BORDER, HUD_BORDER, (ships.get(0).getHealth() / maxHealth) * HEALTH_WIDTH, HUD_HEIGHT - 2 * HUD_BORDER);
		
		gc.setStroke(ShipColors[1]);
		gc.setFill(ShipColors[1]);
		maxHealth = ships.get(1).getMaxHealth();
		gc.strokeRect(this.getWidth() - HEALTH_WIDTH - HUD_BORDER, HUD_BORDER, HEALTH_WIDTH, HUD_HEIGHT - 2 * HUD_BORDER);
		gc.fillRect(this.getWidth() - HEALTH_WIDTH - HUD_BORDER + ((maxHealth - ships.get(1).getHealth()) / maxHealth) * HEALTH_WIDTH, HUD_BORDER, (ships.get(1).getHealth() / 100.0) * HEALTH_WIDTH, HUD_HEIGHT - 2 * HUD_BORDER);
	}
	
	private void drawWins() {
		gc.setStroke(ShipColors[0]);
		gc.setFill(ShipColors[0]);
		for(int i = 0; i < 4; i++) {
			gc.strokeRect(HUD_BORDER + HEALTH_WIDTH + MED_BORDER + i * (WIN_SIZE + WIN_BORDER), (HUD_HEIGHT - WIN_SIZE) / 2, WIN_SIZE, WIN_SIZE);
			if(i < scoreboard.getZeroWin()) {
				gc.fillRect(HUD_BORDER + HEALTH_WIDTH + MED_BORDER + INNER_WIN_BORDER + i * (WIN_SIZE + WIN_BORDER), (HUD_HEIGHT - INNER_WIN_SIZE) / 2, INNER_WIN_SIZE, INNER_WIN_SIZE);
			}
		}

		gc.setStroke(ShipColors[1]);
		gc.setFill(ShipColors[1]);
		for(int i = 0; i < 4; i++) {
			gc.strokeRect(this.getWidth() - (WIN_SIZE + HUD_BORDER + HEALTH_WIDTH + MED_BORDER + i * (WIN_SIZE + WIN_BORDER)), (HUD_HEIGHT - WIN_SIZE) / 2, WIN_SIZE, WIN_SIZE);
			if(i < scoreboard.getOneWin()) {
				gc.fillRect(this.getWidth() - (INNER_WIN_SIZE + HUD_BORDER + HEALTH_WIDTH + MED_BORDER + INNER_WIN_BORDER + i * (WIN_SIZE + WIN_BORDER)), (HUD_HEIGHT - INNER_WIN_SIZE) / 2, INNER_WIN_SIZE, INNER_WIN_SIZE);
			}
		}
	}
	
	private void drawMessage() {
		if(messages.isEmpty()) {
			return;
		}
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setFont(AGENCY_LARGE);
		Message currentMessage = messages.get(0);
		if(currentMessage.duration > 0) {
			currentMessage.duration--;
			gc.setFill(currentMessage.color);
			gc.fillText(currentMessage.text, this.getWidth() / 2, this.getHeight() / 2);
		}
		else {
			messages.remove(currentMessage);
		}
	}
	
	private void drawShips() {
		for (int i = 0; i < numShips; i++) {
			Ship s = ships.get(i);
			gc.setStroke(ShipColors[s.getId()]);
			int x = s.getLocation().x;
			int y = s.getLocation().y;
			gc.strokeOval(x - SHIP_OVAL_SIZE / 2, y - SHIP_OVAL_SIZE / 2, SHIP_OVAL_SIZE, SHIP_OVAL_SIZE);
			
			double[][] shipVertices = MathStuffs.shipVertices(s.getLocation(), s.getRotation(), SHIP_SIDE_LENGTH, SHIP_FRONT_LENGTH);

	        gc.strokePolyline(shipVertices[0], shipVertices[1], shipVertices[0].length);
		}
	}
	
	private void drawBullets() {
		int numBullets = bullets.size();
		for (int i = 0; i < numBullets; i++) {
			Bullet b = bullets.get(i);
			gc.setFill(ShipColors[b.getPlayer()]);
			gc.fillOval((int) b.getX() - (BULLET_SIZE / 2), (int) b.getY() - (BULLET_SIZE / 2), BULLET_SIZE, BULLET_SIZE);
		}
	}
}
