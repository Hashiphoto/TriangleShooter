package gui;

import java.util.ArrayList;

import gameControl.MathStuffs;
import gameControl.Message;
import gameControl.PowerMeter;
import gameControl.PowerMeterPanel;
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
	public static final Color NEUTRAL = Color.WHITE;
	private static final Color SCOREBOARD = Color.web("0x333333");
	private static final int SHIP_SIDE_LENGTH = 35;
	private static final int SHIP_FRONT_LENGTH = 60;
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
	private static final Font AGENCY_AMMO = new Font("Agency FB", 25);
	private static final double LINE_WIDTH = 2;
	private static final int AMMO_OFFSET = 10;
	private static final int METER_BORDER = 40;
	private static final int METER_TOP_BORDER = HUD_HEIGHT + METER_BORDER;
	
	private ArrayList<Ship> ships;
	private ArrayList<Bullet> bullets;
	private ArrayList<Message> messages;
	private ArrayList<Wall> walls;
	private int numShips;
	private GraphicsContext gc;
	private Scoreboard scoreboard;
	private PowerMeterPanel pmp;
	
	public GameCanvas(int width, int height) {
		super(width, height);
		gc = this.getGraphicsContext2D();
		messages = new ArrayList<Message>();
	}

	public void init(ArrayList<Ship> ships, ArrayList<Bullet> bullets, Scoreboard scoreboard, ArrayList<Wall> walls, PowerMeterPanel pmp) {
		this.ships = ships;
		this.bullets = bullets;
		this.numShips = ships.size();
		this.scoreboard = scoreboard;
		this.walls = walls;
		this.pmp = pmp;
		initializeMeters();
		gc.setLineWidth(LINE_WIDTH);
	}
	
	public void repaint() {
		gc.setFill(Color.BLACK);
		gc.fillRect(0, HUD_HEIGHT, this.getWidth(), this.getHeight());
		drawShips();
		drawBullets();
		drawWalls();
		drawScoreboard();
		if(pmp.visible) {
			drawPowerMeters();
		}
		drawMessage();
	}
	
	public void addMessage(Message msg) {
		messages.add(msg);
	}
	
	public boolean messageDisplayed() {
		return !messages.isEmpty();
	}
	
	private void initializeMeters() {
		for(int i = 0; i < pmp.meters.size(); i++) {
			PowerMeter meter = pmp.meters.get(i);
			meter.width = (int) (((this.getWidth() - METER_BORDER) / 6) - METER_BORDER);
			meter.height = (int) (this.getHeight() - METER_TOP_BORDER - METER_BORDER);
			meter.x = METER_BORDER + (i * (meter.width + METER_BORDER));
			meter.y = METER_TOP_BORDER;
		}
	}
	
	private void drawPowerMeters() {
		for(int i = 0; i < pmp.meters.size(); i++) {
			PowerMeter meter = pmp.meters.get(i);
			drawMeter(meter, i);
		}
	}
	
	private void drawMeter(PowerMeter meter, int offset) {
		gc.setFill(SCOREBOARD);
		gc.fillRect(meter.x, meter.y, meter.width, meter.height);
		if(pmp.isDisabled() || meter.disabled) {
			gc.setFill(Color.DARKGRAY);
		}
		else {
			gc.setFill(NEUTRAL);
			gc.setStroke(NEUTRAL);
			gc.strokeRect(meter.x, meter.y, meter.width, meter.height);
		}
		gc.setFont(AGENCY_CLOCK);
		gc.fillText(meter.getName(), meter.x + meter.width/2, meter.y + meter.height - 30);
	}
	
	private void drawWalls() {
		int numWalls = walls.size();
		gc.setStroke(NEUTRAL);
		for(int i = 0; i < numWalls; i++) {
			Wall wall = walls.get(i);
			gc.strokeRect(wall.x1, wall.y1, wall.width(), wall.height());
		}
	}
	
	private void drawScoreboard() {
		gc.setFill(SCOREBOARD);
		
		gc.fillRect(0, 0, this.getWidth(), HUD_HEIGHT);
		gc.setStroke(NEUTRAL);
		gc.strokeRect((this.getWidth() - CLOCK_WIDTH) / 2, 0, CLOCK_WIDTH, HUD_HEIGHT);
		drawGameTime(scoreboard.getGameTime());
		drawHealthBars();
		drawWins();
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
			gc.fillText(currentMessage.text, this.getWidth() / 2, this.getHeight() / 2 + HUD_HEIGHT);
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
			gc.strokeOval(x - s.getRadius() / 2, y - s.getRadius() / 2, s.getRadius(), s.getRadius());
			double[][] shipVertices = MathStuffs.shipVertices(s.getLocation(), s.getRotation(), SHIP_SIDE_LENGTH, SHIP_FRONT_LENGTH);
	        gc.strokePolyline(shipVertices[0], shipVertices[1], shipVertices[0].length);

	        gc.setStroke(NEUTRAL);
	        double radius = s.getPercentageReload() * s.getRadius();
	        gc.strokeOval(x - radius / 2, y - radius / 2, radius, radius);
	        gc.setFill(ShipColors[s.getId()]);
	        gc.setFont(AGENCY_AMMO);
	        gc.fillText(Integer.toString(s.getAmmo()), x, y + AMMO_OFFSET);
		}
	}
	
	private void drawBullets() {
		int numBullets = bullets.size();
		for (int i = 0; i < numBullets; i++) {
			Bullet b = bullets.get(i);
			gc.setFill(ShipColors[b.getPlayer()]);
			gc.fillOval((int) b.getX() - b.getRadius(), (int) b.getY() - b.getRadius(), b.getSize(), b.getSize());
		}
	}
	
	public int getMeterButtonPressed(double d, double e) {
		for(int i = 0; i < pmp.meters.size(); i++) {
			PowerMeter meter = pmp.meters.get(i);
			if(!meter.disabled && d > meter.x && d < meter.x + meter.width
					&& e > meter.y && e < meter.y + meter.height) {
				return i;
			}
		}
		return -1;
	}
}
