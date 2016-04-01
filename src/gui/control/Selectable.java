package gui.control;

import java.awt.Rectangle;
import java.awt.Point;

public interface Selectable {
	public boolean isSelected();
	public void setSelected(boolean b);
	public boolean isWithin(Rectangle r);
	public boolean contains(Point p);
}
