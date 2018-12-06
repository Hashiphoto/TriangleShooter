package levels;

/**
 * This level has a single square in the middle of the screen
 * @author Trent
 *
 */
public class Dot extends Level{
	public Dot() {
		super();
		int dotSize = 75;
		this.addWall(SCREEN_WIDTH / 2 - 75, SCREEN_HEIGHT / 2 - dotSize, SCREEN_WIDTH / 2 + dotSize, SCREEN_HEIGHT / 2 + dotSize);
	}
}
