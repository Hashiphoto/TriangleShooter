package gameControl;

import java.util.ArrayList;

public class PowerMeterPanel {
	public boolean visible;
	private boolean disabled;
	public ArrayList<PowerMeter> meters;
	
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
	
	public boolean isDisabled() {
		return disabled;
	}
	
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	public void enableAllMeters() {
		for (int i = 0; i < meters.size(); i++) {
			meters.get(i).disabled = false;
		}
	}
}
