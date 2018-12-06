package gameControl;

/**
 * This displays the relative values of the attributes of the two ship objects.
 * They are stored in a PowerMeterPanel
 * @author Trent
 *
 */
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
	
	/**
	 * Provide the name for the attribute that will be displayed at the bottom of the column
	 * @param name	The name of the ship attribute
	 */
	public PowerMeter(String name) {
		label = name;
		disabled = false;
		selected = false;
		isBackwards = false;
	}
	
	/**
	 * @return Returns the displayed text
	 */
	public String getName() {
		return label;
	}
	
	/**
	 * Get the percentage filled for Ship 0 of the current value out of the maximum value 
	 * @return	A double in the range 0.0 to 1.0
	 */
	public double getPercentage0() {
		if(isBackwards) {
			return (min - val0) / (min - max);
		}
		return (val0 - min) / (max - min);
	}
	
	/**
	 * Get the percentage filled for Ship 1 of the current value out of the maximum value 
	 * @return	A double in the range 0.0 to 1.0
	 */
	public double getPercentage1() {
		if(isBackwards) {
			return (min - val1) / (min - max);
		}
		return (val1 - min) / (max - min);
	}
	
	/**
	 * Define the upper and lower bounds of this attribute's range. This must be filled out to use
	 * the getPercentage() methods
	 * @param min	The lower bound for the attribute
	 * @param max	The upper bound for the attribute
	 */
	public void setLimits(double min, double max) {
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Sets the current value for ship 0
	 * @param val0	The new value for ship 0
	 */
	public void setVal0(double val0) {
		this.val0 = val0;
	}	
	
	/**
	 * Sets the current value for ship 1
	 * @param val1	The new value for ship 1
	 */
	public void setVal1(double val1) {
		this.val1 = val1;
	}
}
