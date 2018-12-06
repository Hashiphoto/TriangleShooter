package gui;

public class Scoreboard {
	private static final int MATCH_LENGTH = 60;
	
	private long startTime;
	private int zeroWins;
	private int oneWins;
	public Scoreboard() {
		reset();
	}
	
	public void reset() {
		zeroWins = 0;
		oneWins = 0;
	}
	
	public void start() {
		startTime = System.currentTimeMillis();
	}
	
	public int getWins(int id) {
		if(id == 0) {
			return zeroWins;
		}
		return oneWins;
	}

	// Returns how many seconds are left in a match
	public int getGameTime() {
		long timeElapsed = (System.currentTimeMillis() - startTime) / 1000;
		if(timeElapsed >= MATCH_LENGTH) {
			return 0;
		}
		return (int) (MATCH_LENGTH - timeElapsed);
	}
	
	public void win(int winner) {
		if(winner == 0) {
			zeroWins++;
		}
		else if(winner == 1) {
			oneWins++;
		}
	}
}
