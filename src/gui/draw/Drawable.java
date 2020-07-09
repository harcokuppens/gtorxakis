package gui.draw;

import org.w3c.dom.Element;

public interface Drawable {
	public Element getElement();
	public void invalidate();
	public String getAttribute(String cmd);
}
