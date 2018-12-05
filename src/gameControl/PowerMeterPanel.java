package gameControl;

import java.util.ArrayList;

public class PowerMeterPanel {
	public boolean visible;
	public boolean disabled;
	public ArrayList<PowerMeter> meters;
	
	public PowerMeterPanel() {
		visible = false;
		meters = new ArrayList<PowerMeter>();
		meters.add(new PowerMeter("Movement"));
		meters.add(new PowerMeter("Accuracy"));
		meters.add(new PowerMeter("Range"));
		meters.add(new PowerMeter("Damage"));
		meters.add(new PowerMeter("Bullet Size"));
		meters.add(new PowerMeter("Ammo"));
	}
}
