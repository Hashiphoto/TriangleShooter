package gui;

/**
 * This class tracks the wins of the two players and the game time
 * Currently, the game time does not do anything when it reaches 0. The game
 * continues on until someone dies. But it does add some pressure to the match, no?
 * @author Trent
 *
 */
public class Scoreboard {
	private static final int MATCH_LENGTH = 60;
	
	private long startTime;
	private int zeroWins;
	private int oneWins;
	public Scoreboard() {
		reset();
	}
	
	/**
	 * Resets the win record of both players to 0
	 */
	public void reset() {
		zeroWins = 0;
		oneWins = 0;
	}
	
	/**
	 * Starts the countdown timer 
	 */
	public void start() {
		startTime = System.currentTimeMillis();
	}
	
	/**
	 * @param id The id of the player
	 * @return	How many wins that player has
	 */
	public int getWins(int id) {
		if(id == 0) {
			return zeroWins;
		}
		return oneWins;
	}

	/**
	 * Returns an integer representation of how many seconds are left in the match. This is
	 * called by the GameCanvas and drawn on screen
	 * @return	Seconds left on the clock
	 */
	public int getGameTime() {
		long timeElapsed = (System.currentTimeMillis() - startTime) / 1000;
		if(timeElapsed >= MATCH_LENGTH) {
			return 0;
		}
		return (int) (MATCH_LENGTH - timeElapsed);
	}
	
	/**
	 * Increase the number of wins for the given player by 1
	 * @param winner	The id of who won the previous match
	 */
	public void win(int winner) {
		if(winner == 0) {
			zeroWins++;
		}
		else if(winner == 1) {
			oneWins++;
		}
	}
}
