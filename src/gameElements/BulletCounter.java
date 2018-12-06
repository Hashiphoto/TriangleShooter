package gameElements;

/**
 * This class determines the id of the bullet when its fired for the local player's ship only
 * The host has even numbers, and the guest has odd numbers
 * These id's are sent over the network and used to determine which bullets have been destroyed by the colliding
 * with the other player 
 * @author Trent
 *
 */
public abstract class BulletCounter {
	private static int count = 0;
	private static int team = 0;

	/**
	 * Set which team the bullets come from. The host has even numbered id's and the guest has odd
	 * @param team	The id of the player ship
	 */
	public static void setTeam(int team) {
		// Player 0 starts at 0
		// Player 1 starts at 1
		BulletCounter.team = team;
		count = team;
	}

	/**
	 * Gets the next available id
	 * @return	An unused ID number
	 */
	public static int getNextId() {
		count += 2;
		return count;
	}

	/**
	 * Restarts the counting. For the host, this is 0, and the guest, this is 1
	 */
	public static void reset() {
		count = team;
	}
}
