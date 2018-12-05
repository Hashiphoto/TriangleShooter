package gameControl;

public class PowerMeter {
	public boolean disabled;
	public boolean selected;
	public int x;
	public int y;
	public int width;
	public int height;
	private String label;
	private double val0;
	private double val1;
	
	public PowerMeter(String name) {
		label = name;
		disabled = false;
		selected = false;
	}
	
	public String getName() {
		return label;
	}

	public double getValue0() {
		return val0;
	}
	
	public double getValue1() {
		return val1;
	}
	
}
