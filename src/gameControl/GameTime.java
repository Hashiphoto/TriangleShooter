package gameControl;

public class GameTime {
	private double start;
	
	public GameTime() {
		reset();
	}
	
	public void reset() {
		start = System.nanoTime();
	}
	
	public double GetTimeElapsedSeconds() {
		//divided by a billion
		return (System.nanoTime() - start) / 1e9;
	}

}
