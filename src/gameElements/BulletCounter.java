package gameElements;

public class BulletCounter {
	private static int count = 0;
	private static int team = 0;
	
	public static void setTeam(int team) {
		// Player 0 starts at 0
		// Player 1 starts at 1
		BulletCounter.team = team;
		count = team;
	}
	
	public static int getNextId() {
		count += 2;
		return count;
	}
	
	public static void reset() {
		count = team;
	}
}
