package gameElements;

public class Wall {
	public int x1;
	public int y1;
	public int x2;
	public int y2;
	
	public Wall(int x1, int y1, int x2, int y2) {
		if(x1 < x2) {
			this.x1 = x1;
			this.x2 = x2;
		} 
		else {
			this.x2 = x1;
			this.x1 = x2;
		}
		if(y1 < y2) {
			this.y1 = y1;
			this.y2 = y2;
		} 
		else {
			this.y2 = y1;
			this.y1 = y2;
		}
	}
	
	public int width() {
		return x2 - x1;
	}
	
	public int height() {
		return y2 - y1;
	}
	
	public int centerX() {
		return (x2 + x1) / 2;
	}
	
	public int centerY() {
		return (y2 + y1) / 2;
	}
}
