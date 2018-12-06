package levels;

/**
 * This level has four squares in the center of the screen
 * @author Trent
 *
 */
public class FourSquare extends Level{
	public FourSquare() {
		super();
		this.addMirroredWallByWidth(400, 225, 100, 100);
		this.addMirroredWallByWidth(400, 450, 100, 100);
	}
}
