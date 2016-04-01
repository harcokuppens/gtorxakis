package gui.control;

import gui.draw.DrawableComment.ResizeType;

import java.awt.Point;

import util.Vector;

public interface Resizable {
	public void setResizeOffset(Vector v, ResizeType r);

	public void resizeBy(int dx, ResizeType r);
}
