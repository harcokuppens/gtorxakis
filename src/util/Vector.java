package util;

import java.awt.Point;

public class Vector {
	private final int x, y;
	
	public Vector(Point p) {
		this.x = p.x;
		this.y = p.y;
	}
	
	public Vector(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(Point start, Point end, int length) {
		double alpha = Math.atan((end.getX() - start.getX()) / (end.getY() - start.getY()));
		this.x = (int) (Math.cos(alpha) * length);
		this.y = (int) (Math.sin(alpha) * length);
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public static Vector getVectorBetweenPoints(Point p1, Point p2){
		return new Vector(p2.x-p1.x, p2.y-p1.y);
	}
	
	public Vector plus(Vector v){
		int x = this.x + v.x; 
		int y = this.y + v.y; 	
		return new Vector(x, y);
	}
}
