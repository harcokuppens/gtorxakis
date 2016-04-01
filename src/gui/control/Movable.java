package gui.control;

import java.awt.Point;

import util.Vector;

public interface Movable {
	public void moveTo(Point p);
	
	public void moveBy(Vector v);
	
	public void setOffset(Vector v);
}
