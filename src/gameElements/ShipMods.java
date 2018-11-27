package gameElements;

public class ShipMods {
	public static final int GetNumUpgrades() {
		return Upgrades.values().length;
	}

	public enum Upgrades {
		LASER;		//0
	}
}
