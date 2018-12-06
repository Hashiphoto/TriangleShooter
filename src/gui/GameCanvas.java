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

/**
 * The GameCanvas is responsible for drawing the visual representations of the game data
 * @author Trent
 *
 */
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
	
	/**
	 * Instantiate a new GameCanvas. init() must also be called by the GameScene before any drawing can be done
	 * @param width		The width of the canvas in pixels
	 * @param height	The height of the canvas in pixels
	 */
	public GameCanvas(int width, int height) {
		super(width, height);
		gc = this.getGraphicsContext2D();
		messages = new ArrayList<Message>();
	}

	/**
	 * Pass in all the necessary objects from the GameScene that will need to be drawn on screen
	 * @param ships			The player and enemy ship
	 * @param bullets		All existing bullets
	 * @param scoreboard	Holds the win/loss record and game time
	 * @param walls			All of the walls in the current level
	 * @param pmp			The PowerMeterPanel. This is only drawn when its visible property is set to true
	 */
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
	
	/**
	 * Called every 1/60th of a second. The order of the drawing determines which objects end up "on top"
	 */
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
	
	/**
	 * Add a new message to display in the middle of the screen
	 * @param msg	The message to display
	 */
	public void addMessage(Message msg) {
		messages.add(msg);
	}
	
	/**
	 * This method iterates through all the PoweMeters and determines their x and y coordinates,
	 * height and width based on their position in the PMP list
	 */
	private void initializeMeters() {
		for(int i = 0; i < pmp.meters.size(); i++) {
			PowerMeter meter = pmp.meters.get(i);
			meter.width = (int) (((this.getWidth() - METER_BORDER) / 6) - METER_BORDER);
			meter.height = (int) (this.getHeight() - METER_TOP_BORDER - METER_BORDER);
			meter.x = METER_BORDER + (i * (meter.width + METER_BORDER));
			meter.y = METER_TOP_BORDER;
		}
	}
	
	/**
	 * Draws the PowerMeters on screen
	 */
	private void drawPowerMeters() {
		for(int i = 0; i < pmp.meters.size(); i++) {
			PowerMeter meter = pmp.meters.get(i);
			drawMeter(meter, i);
		}
	}
	
	/**
	 * Draw a sinlge PowerMeter
	 * @param meter		The PowerMeter to draw
	 * @param offset	The position of the Meter in the PMP list
	 */
	private void drawMeter(PowerMeter meter, int offset) {
		gc.setFill(SCOREBOARD);
		gc.fillRect(meter.x, meter.y, meter.width, meter.height);

		gc.setFill(ShipColors[0]);
		int side = meter.width / 8;
		int width = meter.width / 4;
		int bottom = 70;
		int height = meter.height - side - bottom;
		double percentage0 = meter.getPercentage0() * height;
		double percentage1 = meter.getPercentage1() * height;
		
		gc.fillRect(meter.x + side, meter.y + side + height - percentage0, meter.width / 4, percentage0);
		gc.setFill(ShipColors[1]);
		gc.fillRect(meter.x + meter.width - width - side, meter.y + side + height - percentage1, meter.width / 4, percentage1);

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
	
	/**
	 * Draw all walls on screen
	 */
	private void drawWalls() {
		int numWalls = walls.size();
		gc.setStroke(NEUTRAL);
		for(int i = 0; i < numWalls; i++) {
			Wall wall = walls.get(i);
			gc.strokeRect(wall.x1, wall.y1, wall.width(), wall.height());
		}
	}
	
	/**
	 * Draw timer, scores, and health in the HUD
	 */
	private void drawScoreboard() {
		gc.setFill(SCOREBOARD);
		
		gc.fillRect(0, 0, this.getWidth(), HUD_HEIGHT);
		gc.setStroke(NEUTRAL);
		gc.strokeRect((this.getWidth() - CLOCK_WIDTH) / 2, 0, CLOCK_WIDTH, HUD_HEIGHT);
		drawGameTime(scoreboard.getGameTime());
		drawHealthBars();
		drawWins();
	}
	
	/**
	 * Draws the text in the top middle of the HUD
	 * @param time	The time left in the match, in seconds
	 */
	private void drawGameTime(int time) {
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setFont(AGENCY_CLOCK);
		gc.setFill(NEUTRAL);
		gc.fillText(Integer.toString(time), this.getWidth() / 2, 45);
	}
	
	/**
	 * Draws player health bars and remaining health
	 */
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
	
	/**
	 * Draws the boxes to indicate how many wins each player has
	 */
	private void drawWins() {
		gc.setStroke(ShipColors[0]);
		gc.setFill(ShipColors[0]);
		for(int i = 0; i < 4; i++) {
			gc.strokeRect(HUD_BORDER + HEALTH_WIDTH + MED_BORDER + i * (WIN_SIZE + WIN_BORDER), (HUD_HEIGHT - WIN_SIZE) / 2, WIN_SIZE, WIN_SIZE);
			if(i < scoreboard.getWins(0)) {
				gc.fillRect(HUD_BORDER + HEALTH_WIDTH + MED_BORDER + INNER_WIN_BORDER + i * (WIN_SIZE + WIN_BORDER), (HUD_HEIGHT - INNER_WIN_SIZE) / 2, INNER_WIN_SIZE, INNER_WIN_SIZE);
			}
		}

		gc.setStroke(ShipColors[1]);
		gc.setFill(ShipColors[1]);
		for(int i = 0; i < 4; i++) {
			gc.strokeRect(this.getWidth() - (WIN_SIZE + HUD_BORDER + HEALTH_WIDTH + MED_BORDER + i * (WIN_SIZE + WIN_BORDER)), (HUD_HEIGHT - WIN_SIZE) / 2, WIN_SIZE, WIN_SIZE);
			if(i < scoreboard.getWins(1)) {
				gc.fillRect(this.getWidth() - (INNER_WIN_SIZE + HUD_BORDER + HEALTH_WIDTH + MED_BORDER + INNER_WIN_BORDER + i * (WIN_SIZE + WIN_BORDER)), (HUD_HEIGHT - INNER_WIN_SIZE) / 2, INNER_WIN_SIZE, INNER_WIN_SIZE);
			}
		}
	}
	
	/**
	 * If there are any messages in the queue, display the first on screen. This method decreases
	 * the duration in that message by one and removes it from the message queue when it reaches -1
	 */
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
	
	/**
	 * Draw the Ship instances on screen
	 */
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
	
	/**
	 * Draw all bullets on screen based on the Bullet size
	 */
	private void drawBullets() {
		for (Bullet b : bullets) {
			gc.setFill(ShipColors[b.getPlayer()]);
			gc.fillOval((int) b.getX() - b.getRadius(), (int) b.getY() - b.getRadius(), b.getSize(), b.getSize());
		}
	}
	
	/**
	 * This detects whether the mouse was clicked inside one of the PowerMeters. It returns -1 if the 
	 * mouse is not inside a PowerMeter or the meter is disabled 
	 * @param mouseX	The x coordinate of the mouse
	 * @param mouseY	The y coordinate of the mouse
	 * @return
	 */
	public int getMeterButtonPressed(double mouseX, double mouseY) {
		for(int i = 0; i < pmp.meters.size(); i++) {
			PowerMeter meter = pmp.meters.get(i);
			if(!meter.disabled && mouseX > meter.x && mouseX < meter.x + meter.width
					&& mouseY > meter.y && mouseY < meter.y + meter.height) {
				return i;
			}
		}
		return -1;
	}
}
