package net.generalised.genedit.model.gn;

public class Point implements GnObjectWithPosition { //TODO: make immutable!

	private int x, y;
	
	public Point() {
		this(0, 0);
	}
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getVisualPositionX() {
		return x;
	}

	public int getVisualPositionY() {
		return y;
	}

	public void setVisualPositionX(int visualPositionX) {
		this.x = visualPositionX;
	}

	public void setVisualPositionY(int visualPositionY) {
		this.y = visualPositionY;
	}
}
