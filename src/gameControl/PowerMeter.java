package gameControl;

public class PowerMeter {
	public boolean disabled;
	public boolean selected;
	public int x;
	public int y;
	public int width;
	public int height;
	public boolean isBackwards;
	private String label;
	private double val0;
	private double val1;
	private double min;
	private double max;
	
	public PowerMeter(String name) {
		label = name;
		disabled = false;
		selected = false;
		isBackwards = false;
	}
	
	public String getName() {
		return label;
	}
	
	public double getPercentage0() {
		if(isBackwards) {
			return (min - val0) / (min - max);
		}
		return (val0 - min) / (max - min);
	}
	
	public double getPercentage1() {
		if(isBackwards) {
			return (min - val1) / (min - max);
		}
		return (val1 - min) / (max - min);
	}
	
	public void setLimits(double min, double max) {
		this.min = min;
		this.max = max;
	}
	
	public void setVal0(double val0) {
		this.val0 = val0;
	}	
	
	public void setVal1(double val1) {
		this.val1 = val1;
	}
}
