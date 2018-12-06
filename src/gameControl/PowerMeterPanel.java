package gameControl;

import java.util.ArrayList;

/**
 * A collection of PowerMeter objects stored in an ArrayList called meters
 * @author Trent
 *
 */
public class PowerMeterPanel {
	public boolean visible;
	private boolean disabled;
	public ArrayList<PowerMeter> meters;
	
	/**
	 * Instantiate a new PowerMeterPanel with static names defined in this class. It is visible by default
	 */
	public PowerMeterPanel() {
		visible = false;
		meters = new ArrayList<PowerMeter>();
		meters.add(new PowerMeter("Movement"));
		meters.add(new PowerMeter("Accuracy"));
		meters.add(new PowerMeter("Range"));
		meters.add(new PowerMeter("Bullet Size"));
		meters.add(new PowerMeter("Ammo"));
		meters.add(new PowerMeter("Reload"));
	}
	
	/**
	 * @return	Returns whether the panel is disabled or not. If it is disabled, it can still be
	 * visible, but not editable.
	 */
	public boolean isDisabled() {
		return disabled;
	}
	
	/**
	 * Disable the PowerMeterPanel from being edited
	 * @param disabled
	 */
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	/**
	 * Iteratively enable all of the PowerMeter objects
	 */
	public void enableAllMeters() {
		for (int i = 0; i < meters.size(); i++) {
			meters.get(i).disabled = false;
		}
	}
}
