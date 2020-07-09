package gui.draw;

import gui.control.DrawController;

import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

public abstract class DrawableElement {
	protected abstract Element getElement();
	protected abstract void invalidate();
	
	/**
	 * The SVGDocument that this DrawableElement is part of
	 */
	protected final SVGDocument doc;
	
	public DrawableElement(SVGDocument doc) {
		this.doc = doc;
	}
	
	public void update(DrawController dc) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				invalidate();
			}
		};
		update(dc, r);
	}
	
	public void update(DrawController dc, Runnable r) {
		dc.addToQueue(r);
	}
}
