package gameElements;

/**
 * A yet unimplemented class that determines what special perks a ship has beyond
 * the statistical boosts
 * @author Trent
 *
 */
public class ShipMods {
	public static final int GetNumUpgrades() {
		return Upgrades.values().length;
	}

	public enum Upgrades {
		LASER;		//0
	}
}
