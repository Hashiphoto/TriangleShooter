package levels;

public class FourSquare extends Level{
	public FourSquare() {
		super();
		this.addMirroredWallByWidth(400, 225, 100, 100);
		this.addMirroredWallByWidth(400, 450, 100, 100);
	}
}
