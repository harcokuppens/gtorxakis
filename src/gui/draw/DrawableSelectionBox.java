package gui.draw;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import core.Session;

public class DrawableSelectionBox {
	private Element element;
	private int x1, y1, x2, y2;
	
	private SVGDocument doc;
	
	public DrawableSelectionBox(SVGDocument doc, int x, int y) {
		this.doc = doc;
		setBeginPosition(x, y);
		buildElement();
	}
	
	public DrawableSelectionBox(SVGDocument doc) {
		this(doc, 0, 0);
	}
	
	public void reinit(SVGDocument doc, int x, int y){
		this.doc = doc;
		setBeginPosition(x, y);
		invalidate();
	}
	
	public void setBeginPosition(int x, int y){
		this.x1 = this.x2 = x;
		this.y1 = this.y2 = y;
	}
	
	
	public void setULCorner(int x, int y) {
		this.x1 = x;
		this.y1 = y;
	}
	
	public void setLRCorner(int x, int y) {
		this.x2 = x;
		this.y2 = y;
		
		invalidate();
	}
	
	public Element getElement() {
		return element;
	}
	
	protected void buildElement() {
		element = doc.createElementNS(SVGDOMImplementation.SVG_NAMESPACE_URI, "rect");
	
		invalidate();
	}
	
	private void invalidate() {
		element.setAttribute("x", String.valueOf(x1 < x2 ? x1 : x2));
		element.setAttribute("y", String.valueOf(y1 < y2 ? y1 : y2));
		element.setAttribute("width", String.valueOf(x1 < x2 ? x2 - x1 : x1 - x2));
		element.setAttribute("height", String.valueOf(y1 < y2 ? y2 - y1 : y1 - y2));
		element.setAttribute("style", "fill:rgb(0,0,255);fill-opacity:0.1;stroke:black;stroke-width:1;");
		element.setAttribute("stroke-dasharray", "5,5");
	}
}
