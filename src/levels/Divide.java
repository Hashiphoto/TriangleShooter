package levels;

public class Divide extends Level {
	public Divide() {
		super();
		this.addWall(SCREEN_WIDTH / 2 - 40, 0, SCREEN_WIDTH / 2 + 40, SCREEN_HEIGHT / 2 - 50);
		this.addWall(SCREEN_WIDTH / 2 - 40, SCREEN_HEIGHT / 2 + 50, SCREEN_WIDTH / 2 + 40, SCREEN_HEIGHT);
	}
}
